package com.hileco.cortex.symbolic.instructions.stack

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

data class SWAP(val topOffsetLeft: Int,
                val topOffsetRight: Int) : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(topOffsetRight, topOffsetLeft)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(
                StackParameter("left", topOffsetLeft),
                StackParameter("right", topOffsetRight)
        )

    override fun toString(): String {
        return "SWAP $topOffsetLeft $topOffsetRight"
    }
}
