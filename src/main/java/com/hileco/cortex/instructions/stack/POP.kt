package com.hileco.cortex.instructions.stack

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.VirtualMachine

class POP : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 1) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        program.stack.pop()
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}