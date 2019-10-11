package com.hileco.cortex.vm.instructions.jumps

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier

class EXIT : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(InstructionModifier.PROGRAM_CONTEXT)
}
