package com.hileco.cortex.symbolic.instructions.io


import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.*

abstract class IoInstruction(val programStoreZone: ProgramStoreZone) : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> listOf(STACK, MEMORY)
            ProgramStoreZone.DISK -> listOf(STACK, DISK)
            ProgramStoreZone.CALL_DATA -> listOf(STACK, CALL_DATA)
            ProgramStoreZone.CODE -> listOf(STACK, CODE)
        }

    override fun toString(): String {
        return "${this.javaClass.simpleName} $programStoreZone"
    }
}
