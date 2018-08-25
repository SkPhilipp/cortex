package com.hileco.cortex.instructions.bits;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BITWISE_AND extends BitInstruction {
    @Override
    public byte innerExecute(byte left, byte right) {
        byte result = left;
        result &= right;
        return result;
    }
}