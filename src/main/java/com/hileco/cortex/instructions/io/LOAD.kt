package com.hileco.cortex.instructions.io

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class LOAD(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val addressBytes = programContext.stack.pop()
        val address = BigInteger(addressBytes)
        val storage: LayeredBytes = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> programContext.memory
            ProgramStoreZone.DISK -> programContext.program.storage
            ProgramStoreZone.CALL_DATA -> programContext.callData
        }
        if (address.toInt() * SIZE + SIZE > storage.size) {
            throw ProgramException(ProgramException.Reason.STORAGE_ACCESS_OUT_OF_BOUNDS)
        }
        val bytes = storage.read(address.toInt() * SIZE, SIZE)
        programContext.stack.push(bytes)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val addressExpression = programContext.stack.pop() as? Expression.Value
                ?: throw UnsupportedOperationException("Non-concrete address loading is not supported for symbolic execution")
        val address = BigInteger.valueOf(addressExpression.constant)
        val storage: LayeredMap<BigInteger, Expression> = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> programContext.memory
            ProgramStoreZone.DISK -> programContext.program.storage
            ProgramStoreZone.CALL_DATA -> programContext.callData
        }
        val expression = storage[address] ?: Expression.Value(0)
        programContext.stack.push(expression)
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        const val SIZE = 32
    }
}

