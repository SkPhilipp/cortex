package com.hileco.cortex.instructions.io

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.ProgramException.Reason.STORAGE_ACCESS_OUT_OF_BOUNDS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class SAVE(programStoreZone: ProgramStoreZone) : IoInstruction(programStoreZone) {
    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, BYTES)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val addressBytes = programContext.stack.pop()
        val address = BigInteger(addressBytes)
        val storage: LayeredBytes = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> programContext.memory
            ProgramStoreZone.DISK -> programContext.program.storage
            ProgramStoreZone.CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: $programStoreZone")
        }
        val bytes = programContext.stack.pop()
        val alignmentOffset = LOAD.SIZE - bytes.size
        if (address.toInt() * LOAD.SIZE + alignmentOffset + bytes.size > storage.size) {
            throw ProgramException(STORAGE_ACCESS_OUT_OF_BOUNDS)
        }
        storage.write(address.toInt() * LOAD.SIZE + alignmentOffset, bytes)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val addressExpression = programContext.stack.pop() as? Expression.Value
                ?: throw UnsupportedOperationException("Non-concrete address loading is not supported for symbolic execution")
        val storage: LayeredMap<BigInteger, Expression> = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> programContext.memory
            ProgramStoreZone.DISK -> programContext.program.storage
            ProgramStoreZone.CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: $programStoreZone")
        }
        val valueExpression = programContext.stack.pop()
        storage[addressExpression.constant.toBigInteger()] = valueExpression
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val BYTES = StackParameter("bytes", 1)
    }
}
