package com.hileco.cortex.instructions.stack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

data class SWAP(val topOffsetLeft: Int,
                val topOffsetRight: Int) : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(topOffsetRight, topOffsetLeft)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(
                StackParameter("left", topOffsetLeft),
                StackParameter("right", topOffsetRight)
        )

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() <= topOffsetLeft || programContext.stack.size() <= topOffsetRight) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        programContext.stack.swap(topOffsetLeft, topOffsetRight)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() <= topOffsetLeft || programContext.stack.size() <= topOffsetRight) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        programContext.stack.swap(topOffsetLeft, topOffsetRight)
    }

    override fun toString(): String {
        return "SWAP $topOffsetLeft $topOffsetRight"
    }
}
