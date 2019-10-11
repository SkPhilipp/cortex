package com.hileco.cortex.vm.instructions.conditions


import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.STACK
import com.hileco.cortex.vm.instructions.StackParameter

abstract class ConditionInstruction : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(LEFT, RIGHT)

    companion object {
        val LEFT = StackParameter("left", 0)
        val RIGHT = StackParameter("right", 1)
    }
}
