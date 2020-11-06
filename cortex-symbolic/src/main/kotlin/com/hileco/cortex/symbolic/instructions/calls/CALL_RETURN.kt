package com.hileco.cortex.symbolic.instructions.calls

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.*
import com.hileco.cortex.symbolic.instructions.StackParameter

class CALL_RETURN : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(OFFSET, SIZE)

    companion object {
        val OFFSET = StackParameter("offset", 0)
        val SIZE = StackParameter("size", 1)
    }
}
