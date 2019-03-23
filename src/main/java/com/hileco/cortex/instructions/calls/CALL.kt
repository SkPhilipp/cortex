package com.hileco.cortex.instructions.calls

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.*
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class CALL : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(RECIPIENT_ADDRESS, VALUE_TRANSFERRED, IN_OFFSET, IN_SIZE, OUT_OFFSET, OUT_SIZE)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 6) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val recipientAddress = BigInteger(programContext.stack.pop())
        val valueTransferred = BigInteger(programContext.stack.pop())
        val inOffset = BigInteger(programContext.stack.pop())
        val inSize = BigInteger(programContext.stack.pop())
        val outOffset = BigInteger(programContext.stack.pop())
        val outSize = BigInteger(programContext.stack.pop())
        programContext.returnDataOffset = outOffset
        programContext.returnDataSize = outSize
        val recipient = virtualMachine.atlas[recipientAddress] ?: throw ProgramException(CALL_RECIPIENT_MISSING)
        val sourceAddress = programContext.program.address
        recipient.transfers.push(sourceAddress to valueTransferred)
        val newContext = ProgramContext(recipient)
        val inputData = programContext.memory.read(inOffset.toInt() * LOAD.SIZE, inSize.toInt())
        newContext.callData.clear()
        newContext.callData.write(0, inputData)
        virtualMachine.programs.add(newContext)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 6) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val recipientAddress = programContext.stack.pop()
        val valueTransferred = programContext.stack.pop()
        val inOffset = programContext.stack.pop()
        val inSize = programContext.stack.pop()
        val outOffset = programContext.stack.pop()
        val outSize = programContext.stack.pop()
        if (inOffset != Expression.Value(0)
                || inSize != Expression.Value(0)
                || outOffset != Expression.Value(0)
                || outSize != Expression.Value(0)) {
            throw UnsupportedOperationException("Memory transfer is not supported for symbolic execution")
        }
        if (recipientAddress !is Expression.Value) {
            throw UnsupportedOperationException("Non-concrete address calling is not supported for symbolic execution")
        }
        val recipient = virtualMachine.atlas[recipientAddress.constant.toBigInteger()] ?: throw ProgramException(CALL_RECIPIENT_MISSING)
        val sourceAddress = programContext.program.address
        recipient.transfers.push(Expression.Value(sourceAddress.toLong()) to valueTransferred)
        val newContext = SymbolicProgramContext(recipient)
        virtualMachine.programs.add(newContext)
    }

    companion object {
        val RECIPIENT_ADDRESS = StackParameter("recipientAddress", 0)
        val VALUE_TRANSFERRED = StackParameter("valueTransferred", 1)
        val IN_OFFSET = StackParameter("inOffset", 2)
        val IN_SIZE = StackParameter("inSize", 3)
        val OUT_OFFSET = StackParameter("outOffset", 4)
        val OUT_SIZE = StackParameter("outSize", 5)
    }
}
