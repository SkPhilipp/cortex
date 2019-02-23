package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine

class JUMP_DESTINATION : Instruction() {
    override fun execute(process: VirtualMachine, program: ProgramContext) {}
}
