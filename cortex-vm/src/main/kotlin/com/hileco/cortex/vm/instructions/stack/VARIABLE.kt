package com.hileco.cortex.vm.instructions.stack


import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.STACK

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
