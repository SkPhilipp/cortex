package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

class HALT(val reason: ProgramException.Reason) : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(ProgramZone.PROGRAM_CONTEXT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        throw ProgramException(reason)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        throw ProgramException(reason)
    }

    override fun toString(): String {
        return "HALT $reason"
    }
}
