package com.hileco.cortex.symbolic.instructions.stack

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

class POP : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}