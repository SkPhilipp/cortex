package com.hileco.cortex.symbolic.instructions.conditions

import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK
import com.hileco.cortex.symbolic.instructions.StackParameter

class IS_ZERO : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
