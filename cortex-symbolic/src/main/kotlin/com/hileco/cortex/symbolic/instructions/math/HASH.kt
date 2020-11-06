package com.hileco.cortex.symbolic.instructions.math

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

data class HASH(val method: String) : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun toString(): String {
        return "HASH $method"
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
