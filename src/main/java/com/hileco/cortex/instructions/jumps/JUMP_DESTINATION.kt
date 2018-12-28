package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

class JUMP_DESTINATION : Instruction() {
    override fun execute(process: VirtualMachine, program: ProgramContext) {}
}
