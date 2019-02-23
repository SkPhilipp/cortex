package com.hileco.cortex.instructions.stack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

class POP : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        programContext.stack.pop()
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        programContext.stack.pop()
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}