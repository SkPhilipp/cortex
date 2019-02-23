package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

class NOOP : Instruction() {
    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {}

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {}
}
