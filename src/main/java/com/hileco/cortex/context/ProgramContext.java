package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ProgramContext {
    private int instructionsExecuted;
    private int instructionLimit;
    private int instructionPosition;
    private LayeredStack<byte[]> stack;
    private LayeredBytes memory;
    private Program program;
    private BigInteger returnDataOffset;
    private BigInteger returnDataSize;
    private LayeredBytes callData;

    public ProgramContext(Program program) {
        instructionsExecuted = 0;
        instructionPosition = 0;
        instructionLimit = 1000000;
        stack = new LayeredStack<>();
        memory = new LayeredBytes();
        callData = new LayeredBytes();
        returnDataOffset = BigInteger.ZERO;
        returnDataSize = BigInteger.ZERO;
        this.program = program;
    }
}