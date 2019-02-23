package com.hileco.cortex.instructions.bits

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.VirtualMachine

abstract class BitInstruction : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(LEFT, RIGHT)

    abstract fun innerExecute(left: Byte, right: Byte): Byte

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 2) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val left = program.stack.pop()
        val right = program.stack.pop()
        val result = ByteArray(Math.max(left.size, right.size))

        for (i in result.indices) {
            val leftByte = if (i < left.size) left[i] else 0
            val rightByte = if (i < right.size) right[i] else 0
            result[i] = innerExecute(leftByte, rightByte)
        }
        program.stack.push(result)
    }

    companion object {
        val LEFT = StackParameter("left", 0)
        val RIGHT = StackParameter("right", 1)
    }
}
