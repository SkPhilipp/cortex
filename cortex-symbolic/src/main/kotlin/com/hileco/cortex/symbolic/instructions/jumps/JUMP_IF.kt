package com.hileco.cortex.symbolic.instructions.jumps

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.INSTRUCTION_POSITION
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

class JUMP_IF : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK, INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, CONDITION)

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val CONDITION = StackParameter("condition", 1)
    }
}
