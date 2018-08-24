package com.hileco.cortex.instructions.bits;

public class BITWISE_XOR extends BitInstruction {
    public byte innerExecute(byte left, byte right) {
        byte result = left;
        result ^= right;
        return result;
    }
}