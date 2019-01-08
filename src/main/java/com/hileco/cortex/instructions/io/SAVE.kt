package com.hileco.cortex.instructions.io

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes
import java.math.BigInteger

class SAVE(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, BYTES)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 2) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val addressBytes = program.stack.pop()!!
        val address = BigInteger(addressBytes)
        val storage: LayeredBytes = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> program.memory
            ProgramStoreZone.DISK -> program.program.storage
            ProgramStoreZone.CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: $programStoreZone")
        }
        val bytes = program.stack.pop()!!
        val alignmentOffset = LOAD.SIZE - bytes.size
        if (address.toInt() * LOAD.SIZE + alignmentOffset + bytes.size > storage.size) {
            throw ProgramException(program, ProgramException.Reason.STORAGE_ACCESS_OUT_OF_BOUNDS)
        }
        storage.write(address.toInt() * LOAD.SIZE + alignmentOffset, bytes)
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val BYTES = StackParameter("bytes", 1)
    }
}
