package com.hileco.cortex.symbolic.instructions.jumps

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier

class EXIT : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(InstructionModifier.PROGRAM_CONTEXT)
}
