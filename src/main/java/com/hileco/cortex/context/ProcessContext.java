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
        this.programs = new LayeredStack<>();
        for (ProgramContext programContext : programContexts) {
            this.programs.push(programContext);
        }
        this.atlas = new LayeredMap<>();
        this.stackLimit = Long.MAX_VALUE;
        this.overflowLimit = NUMERICAL_LIMIT;
        this.underflowLimit = NUMERICAL_LIMIT;
        this.instructionsExecuted = 0;
        this.instructionLimit = INSTRUCTION_LIMIT;
    }
}
