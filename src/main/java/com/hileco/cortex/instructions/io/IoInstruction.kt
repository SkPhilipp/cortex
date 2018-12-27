package com.hileco.cortex.instructions.io


import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.*

abstract class IoInstruction(val programStoreZone: ProgramStoreZone) : Instruction() {

    override val instructionModifiers: List<ProgramZone>
        get() = when (programStoreZone) {
            ProgramStoreZone.MEMORY -> listOf(STACK, MEMORY)
            ProgramStoreZone.DISK -> listOf(STACK, DISK)
            ProgramStoreZone.CALL_DATA -> listOf(STACK, CALL_DATA)
        }

    override fun toString(): String {
        return String.format("%s %s", this.javaClass.simpleName, programStoreZone)
    }
}
