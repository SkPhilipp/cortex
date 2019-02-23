package com.hileco.cortex.instructions.stack


import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.VirtualMachine

data class DUPLICATE(val topOffset: Int) : Instruction() {
    private val input: StackParameter = StackParameter("input", topOffset)

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(input)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() <= topOffset) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        program.stack.duplicate(topOffset)
        if (program.stack.size() > process.stackLimit) {
            throw ProgramException(program, STACK_LIMIT_REACHED)
        }
    }

    override fun toString(): String {
        return "DUPLICATE $topOffset"
    }
}
