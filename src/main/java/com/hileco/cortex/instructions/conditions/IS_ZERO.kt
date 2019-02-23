package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.VirtualMachine

class IS_ZERO : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 1) {
            throw ProgramException(program, ProgramException.Reason.STACK_TOO_FEW_ELEMENTS)
        }
        val top = program.stack.pop()
        val isZero = !top.any { byte -> byte > 0 }
        val resultReference = if (isZero) ConditionInstruction.TRUE else ConditionInstruction.FALSE
        program.stack.push(resultReference.clone())
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
