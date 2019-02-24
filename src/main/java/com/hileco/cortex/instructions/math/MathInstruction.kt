package com.hileco.cortex.instructions.math


import com.hileco.cortex.constraints.expressions.Expression
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
import java.math.BigInteger

abstract class MathInstruction : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(LEFT, RIGHT)

    abstract fun calculate(left: BigInteger, right: BigInteger): BigInteger

    abstract fun calculate(left: Expression, right: Expression): Expression

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val left = BigInteger(programContext.stack.pop())
        val right = BigInteger(programContext.stack.pop())
        val result = calculate(left, right)
        programContext.stack.push(result.toByteArray())
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val left = programContext.stack.pop()
        val right = programContext.stack.pop()
        val result = calculate(left, right)
        programContext.stack.push(result)
    }

    companion object {
        val LEFT = StackParameter("left", 0)
        val RIGHT = StackParameter("right", 1)
    }
}