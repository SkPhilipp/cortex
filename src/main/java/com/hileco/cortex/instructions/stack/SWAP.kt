package com.hileco.cortex.instructions.stack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.VirtualMachine

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

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() <= topOffsetLeft || program.stack.size() <= topOffsetRight) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        program.stack.swap(topOffsetLeft, topOffsetRight)
    }

    override fun toString(): String {
        return "SWAP $topOffsetLeft $topOffsetRight"
    }
}
