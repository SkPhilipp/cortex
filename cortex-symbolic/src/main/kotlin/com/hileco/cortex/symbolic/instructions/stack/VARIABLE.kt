package com.hileco.cortex.symbolic.instructions.stack


import com.hileco.cortex.symbolic.instructions.Instruction
import com.hileco.cortex.symbolic.instructions.InstructionModifier
import com.hileco.cortex.symbolic.instructions.InstructionModifier.STACK

data class VARIABLE(val executionVariable: ExecutionVariable) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override fun toString(): String {
        return "VARIABLE $executionVariable"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is VARIABLE) executionVariable == other.executionVariable else false
    }

    override fun hashCode(): Int {
        return executionVariable.hashCode()
    }
}
