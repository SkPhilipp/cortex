package com.hileco.cortex.instructions.bits;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BITWISE_OR extends BitInstruction {
    public byte innerExecute(byte left, byte right) {
        byte result = left;
        result |= right;
        return result;
    }
}