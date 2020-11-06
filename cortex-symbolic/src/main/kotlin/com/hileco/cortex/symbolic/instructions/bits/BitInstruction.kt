package com.hileco.cortex.symbolic.instructions.bits

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

abstract class BitInstruction : Instruction() {
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
