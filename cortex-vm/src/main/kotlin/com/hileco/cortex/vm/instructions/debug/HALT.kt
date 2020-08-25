package com.hileco.cortex.vm.instructions.debug

import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier

class HALT(val reason: ProgramException.Reason, private val description: String = "") : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(InstructionModifier.PROGRAM_CONTEXT)

    override fun toString(): String {
        return if (description.isEmpty()) {
            "HALT $reason"
        } else {
            "HALT $reason ($description)"
        }
    }
}
