package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

class DROP(val elements: Int) : Instruction() {
    override val stackParameters = IntRange(0, elements - 1).map { StackParameter("input", it) }.toList()

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(ProgramZone.STACK)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < elements) {
            throw ProgramException(ProgramException.Reason.STACK_UNDERFLOW)
        }
        repeat(elements) {
            programContext.stack.pop()
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < elements) {
            throw ProgramException(ProgramException.Reason.STACK_UNDERFLOW)
        }
        repeat(elements) {
            programContext.stack.pop()
        }
    }

    override fun toString(): String {
        return "DROP $elements"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DROP) elements == other.elements else false
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }

}
