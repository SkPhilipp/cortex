package com.hileco.cortex.vm.instructions.calls

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.*
import com.hileco.cortex.vm.instructions.StackParameter

class CALL : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(GAS, RECIPIENT_ADDRESS, VALUE_TRANSFERRED, IN_OFFSET, IN_SIZE, OUT_OFFSET, OUT_SIZE)

    companion object {
        val GAS = StackParameter("gas", 0)
        val RECIPIENT_ADDRESS = StackParameter("recipientAddress", 1)
        val VALUE_TRANSFERRED = StackParameter("valueTransferred", 2)
        val IN_OFFSET = StackParameter("inOffset", 3)
        val IN_SIZE = StackParameter("inSize", 4)
        val OUT_OFFSET = StackParameter("outOffset", 5)
        val OUT_SIZE = StackParameter("outSize", 6)
    }
}
