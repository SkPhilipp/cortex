package com.hileco.cortex.vm.instructions.stack


import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.STACK
import java.math.BigInteger

data class PUSH(val bytes: ByteArray,
                override val width: Int = 1) : Instruction() {

    constructor(value: Long) : this(value.toBigInteger().toByteArray())

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    val value: Long
        get() = BigInteger(bytes).toLong()

    override fun toString(): String {
        return "PUSH ${BigInteger(bytes)} (width=$width)"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PUSH) bytes.contentEquals(other.bytes) and (width == other.width) else false
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
