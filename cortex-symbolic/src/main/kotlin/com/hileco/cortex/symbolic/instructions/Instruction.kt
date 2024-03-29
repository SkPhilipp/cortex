package com.hileco.cortex.symbolic.instructions


abstract class Instruction {
    open val stackAdds: List<Int>
        get() = listOf()

    open val instructionModifiers: List<InstructionModifier>
        get() = listOf()

    open val stackParameters: List<StackParameter>
        get() = listOf()

    open val width: Int
        get() = 1

    override fun equals(other: Any?): Boolean {
        return other != null && other::class == this::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}
