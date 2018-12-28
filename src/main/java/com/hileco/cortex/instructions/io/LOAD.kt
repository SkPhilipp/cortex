package com.hileco.cortex.instructions.io

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes
import java.math.BigInteger

class LOAD(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 1) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val addressBytes = program.stack.pop()
        val address = BigInteger(addressBytes)
        val layeredBytes: LayeredBytes = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> program.memory
            ProgramStoreZone.DISK -> program.program.storage
            ProgramStoreZone.CALL_DATA -> program.callData
        }
        val bytes = layeredBytes.read(address.toInt(), SIZE)
        program.stack.push(bytes)
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        private const val SIZE = 32
    }
}

