package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.*
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

class IS_ZERO : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val top = programContext.stack.pop()
        val isZero = !top.any { byte -> byte > 0 }
        val resultReference = if (isZero) ConditionInstruction.TRUE else ConditionInstruction.FALSE
        programContext.stack.push(resultReference.clone())
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val expression = programContext.stack.pop()
        val result = innerExecute(expression)
        programContext.stack.push(result)
    }

    fun innerExecute(expression: Expression): Expression {
        return if (expression is Value) {
            if (expression.constant == 0L) True else False
        } else {
            IsZero(expression)
        }
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
