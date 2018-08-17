package com.hileco.cortex.instructions.operations.bits;

public class BITWISE_XOR extends BitOperation {
    public byte innerExecute(byte left, byte right) {
        byte result = left;
        result ^= right;
        return result;
    }
}