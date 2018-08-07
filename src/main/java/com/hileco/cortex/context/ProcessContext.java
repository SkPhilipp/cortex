package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ProcessContext {
    public static final int INSTRUCTION_LIMIT = 1_000_000;
    public static final BigInteger NUMERICAL_LIMIT = new BigInteger(new byte[]{2}).pow(256).subtract(BigInteger.ONE);

    private LayeredStack<ProgramContext> programs;
    private LayeredMap<BigInteger, Program> atlas;
    private BigInteger overflowLimit;
    private BigInteger underflowLimit;
    private long stackLimit;
    private int instructionsExecuted;
    private int instructionLimit;

    public ProcessContext(ProgramContext... programContexts) {
        programs = new LayeredStack<>();
        for (ProgramContext programContext : programContexts) {
            programs.push(programContext);
        }
        atlas = new LayeredMap<>();
        stackLimit = Long.MAX_VALUE;
        overflowLimit = NUMERICAL_LIMIT;
        underflowLimit = NUMERICAL_LIMIT;
        instructionsExecuted = 0;
        instructionLimit = INSTRUCTION_LIMIT;
    }
}
