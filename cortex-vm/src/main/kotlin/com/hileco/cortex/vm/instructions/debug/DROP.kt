package com.hileco.cortex.vm.instructions.debug

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.StackParameter

class DROP(val elements: Int) : Instruction() {
    override val stackParameters = IntRange(0, elements - 1).map { StackParameter("input", it) }.toList()

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(InstructionModifier.STACK)

    override fun toString(): String {
        return "DROP $elements"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DROP) elements == other.elements else false
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}
