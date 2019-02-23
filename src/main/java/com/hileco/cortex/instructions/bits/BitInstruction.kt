package com.hileco.cortex.instructions.bits

import com.hileco.cortex.constraints.expressions.Expression
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

abstract class BitInstruction : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(LEFT, RIGHT)

    abstract fun innerExecute(left: Byte, right: Byte): Byte

    abstract fun innerExecute(left: Expression, right: Expression): Expression

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val left = programContext.stack.pop()
        val right = programContext.stack.pop()
        val result = ByteArray(Math.max(left.size, right.size))

        for (i in result.indices) {
            val leftByte = if (i < left.size) left[i] else 0
            val rightByte = if (i < right.size) right[i] else 0
            result[i] = innerExecute(leftByte, rightByte)
        }
        programContext.stack.push(result)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val left = programContext.stack.pop()
        val right = programContext.stack.pop()
        val result = innerExecute(left, right)
        programContext.stack.push(result)
    }

    companion object {
        val LEFT = StackParameter("left", 0)
        val RIGHT = StackParameter("right", 1)
    }
}
