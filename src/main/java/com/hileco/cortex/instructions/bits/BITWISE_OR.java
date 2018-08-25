package com.hileco.cortex.instructions.bits;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BITWISE_OR extends BitInstruction {
    @Override
    public byte innerExecute(byte left, byte right) {
        var result = left;
        result |= right;
        return result;
    }
}