package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine

class NOOP : Instruction() {
    override fun execute(process: VirtualMachine, program: ProgramContext) {}
}
