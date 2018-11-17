package com.hileco.cortex.vm;

import com.hileco.cortex.vm.layer.LayeredBytes;
import com.hileco.cortex.vm.layer.LayeredStack;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ProgramContext {
    public static final int INSTRUCTION_LIMIT = 1_000_000;

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
        this.instructionsExecuted = 0;
        this.instructionPosition = 0;
        this.instructionLimit = INSTRUCTION_LIMIT;
        this.stack = new LayeredStack<>();
        this.memory = new LayeredBytes();
        this.callData = new LayeredBytes();
        this.returnDataOffset = BigInteger.ZERO;
        this.returnDataSize = BigInteger.ZERO;
        this.program = program;
    }
}