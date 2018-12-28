package com.hileco.cortex.instructions.calls

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.*
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

class CALL : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(RECIPIENT_ADDRESS, VALUE_TRANSFERRED, IN_OFFSET, IN_SIZE, OUT_OFFSET, OUT_SIZE)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 6) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val recipientAddress = BigInteger(program.stack.pop()!!)
        val valueTransferred = BigInteger(program.stack.pop()!!)
        val inOffset = BigInteger(program.stack.pop()!!)
        val inSize = BigInteger(program.stack.pop()!!)
        val outOffset = BigInteger(program.stack.pop()!!)
        val outSize = BigInteger(program.stack.pop()!!)
        program.returnDataOffset = outOffset
        program.returnDataSize = outSize
        val recipient = process.atlas[recipientAddress] ?: throw ProgramException(program, CALL_RECIPIENT_MISSING)
        val sourceAddress = program.program.address
        recipient.transfers.push(sourceAddress to valueTransferred)
        val newContext = ProgramContext(recipient)
        val inputData = program.memory.read(inOffset.toInt(), inSize.toInt())
        newContext.callData.clear()
        newContext.callData.write(0, inputData)
        process.programs.push(newContext)
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
