package com.hileco.cortex.instructions.conditions


import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.VirtualMachine

abstract class ConditionInstruction : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(LEFT, RIGHT)

    internal abstract fun innerExecute(left: ByteArray, right: ByteArray): Boolean

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 2) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val left = program.stack.pop()!!
        val right = program.stack.pop()!!
        val equals = innerExecute(left, right)
        program.stack.push(if (equals) TRUE.clone() else FALSE.clone())
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }

    companion object {
        val LEFT = StackParameter("left", 0)
        val RIGHT = StackParameter("right", 1)
        val TRUE = byteArrayOf(1)
        val FALSE = byteArrayOf(0)
    }
}
