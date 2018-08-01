package com.hileco.cortex.fuzzer.choices;

public enum FuzzExpression implements Chanced {
    /**
     * All in-stack mathematical operations using only elements on the stack.
     */
    EXPRESSION(50D),
    /**
     * {@link #EXPRESSION} pushing one constant value to the stack.
     */
    CONSTANT_EXPRESSION(50D),

    SWAP(10D),
    DUP(50D),
    POP(5D),

    OVERFLOW(10D),
    UNDERFLOW(10D),

    PUSH_CALL_DATA(30D),
    PUSH_SAVED(30D),
    PUSH_ENVIRONMENTAL(30D),

    IF(30D),

    WHILE(5D),

    INTERNAL_JUMP(10D),
    INTERNAL_CALL(20D),

    /**
     * Generally only uses addresses which have been used for SAVEs
     */
    LOAD(10D),
    SAVE(10D),

    DEAD_END_EXIT(1D),

    JUMP_ANYWHERE(1D);

    private double chance;

    FuzzExpression(double chance) {
        this.chance = chance;
    }

    @Override
    public double chance() {
        return chance;
    }
}
