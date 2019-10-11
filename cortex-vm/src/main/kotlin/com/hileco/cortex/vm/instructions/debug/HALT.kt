package com.hileco.cortex.vm.instructions.debug

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.ProgramException

class HALT(val reason: ProgramException.Reason) : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(InstructionModifier.PROGRAM_CONTEXT)

    override fun toString(): String {
        return "HALT $reason"
    }
}
