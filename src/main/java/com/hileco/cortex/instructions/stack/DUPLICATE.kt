package com.hileco.cortex.instructions.stack


import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_OVERFLOW
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramConstants.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

data class DUPLICATE(val topOffset: Int) : Instruction() {
    private val input: StackParameter = StackParameter("input", topOffset)

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(input)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() <= topOffset) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        programContext.stack.duplicate(topOffset)
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() <= topOffset) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        programContext.stack.duplicate(topOffset)
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun toString(): String {
        return "DUPLICATE $topOffset"
    }
}
