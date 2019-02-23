package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramZone
import com.hileco.cortex.vm.concrete.VirtualMachine

class EXIT : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(ProgramZone.PROGRAM_CONTEXT)

    override fun execute(process: VirtualMachine, program: ProgramContext) {
        process.programs.pop()
    }
}
