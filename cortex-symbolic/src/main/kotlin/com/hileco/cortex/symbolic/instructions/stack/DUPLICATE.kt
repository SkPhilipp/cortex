package com.hileco.cortex.symbolic.instructions.stack


import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

data class DUPLICATE(val topOffset: Int) : Instruction() {
    private val input: StackParameter = StackParameter("input", topOffset)

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(input)

    override fun toString(): String {
        return "DUPLICATE $topOffset"
    }
}
