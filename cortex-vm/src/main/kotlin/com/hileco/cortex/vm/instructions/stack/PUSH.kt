package com.hileco.cortex.vm.instructions.stack


import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.STACK

data class PUSH(val value: BackedInteger,
                override val width: Int = 1) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override fun toString(): String {
        return "PUSH $value (width=$width)"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PUSH) value == other.value && (width == other.width) else false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
