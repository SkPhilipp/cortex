package com.hileco.cortex.fuzzer

enum class FuzzExpression(private val chance: Double) : Chanced {
    /**
     * All in-stack mathematical operations using only elements on the stack.
     */
    EXPRESSION(50.0),
    /**
     * [.EXPRESSION] pushing one constant value to the stack.
     */
    CONSTANT_EXPRESSION(50.0),

    SWAP(10.0),
    DUP(50.0),
    POP(5.0),

    OVERFLOW(10.0),
    UNDERFLOW(10.0),

    PUSH_CALL_DATA(30.0),
    PUSH_SAVED(30.0),
    PUSH_ENVIRONMENTAL(30.0),

    IF(30.0),

    WHILE(5.0),

    INTERNAL_JUMP(10.0),
    INTERNAL_CALL(20.0),

    /**
     * Generally only uses addresses which have been used for SAVEs
     */
    LOAD(10.0),
    SAVE(10.0),

    DEAD_END_EXIT(1.0),

    JUMP_ANYWHERE(1.0);

    override fun chance(): Double {
        return this.chance
    }
}
