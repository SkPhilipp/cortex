package com.hileco.cortex.vm.instructions.io


import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.*

abstract class IoInstruction(val programStoreZone: ProgramStoreZone) : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> listOf(STACK, MEMORY)
            ProgramStoreZone.DISK -> listOf(STACK, DISK)
            ProgramStoreZone.CALL_DATA -> listOf(STACK, CALL_DATA)
        }

    override fun toString(): String {
        return "${this.javaClass.simpleName} $programStoreZone"
    }
}
