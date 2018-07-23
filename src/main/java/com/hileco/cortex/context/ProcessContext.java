package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

    public static final BigInteger NUMERICAL_LIMIT = new BigInteger(new byte[]{2}).pow(256).subtract(BigInteger.ONE);

    private LayeredStack<ProgramContext> programs;
    private Map<String, Program> atlas;
    private BigInteger overflowLimit;
    private BigInteger underflowLimit;
    private long stackLimit;

    public ProcessContext(ProgramContext programContext) {
        this.programs = new LayeredStack<>();
        this.programs.push(programContext);
        this.atlas = new HashMap<>();
        this.stackLimit = Long.MAX_VALUE;
        this.overflowLimit = NUMERICAL_LIMIT;
        this.underflowLimit = NUMERICAL_LIMIT;
    }

    public LayeredStack<ProgramContext> getPrograms() {
        return programs;
    }

    public void setPrograms(LayeredStack<ProgramContext> programs) {
        this.programs = programs;
    }

    public Map<String, Program> getAtlas() {
        return atlas;
    }

    public void setAtlas(Map<String, Program> atlas) {
        this.atlas = atlas;
    }

    public BigInteger getOverflowLimit() {
        return overflowLimit;
    }

    public void setOverflowLimit(BigInteger overflowLimit) {
        this.overflowLimit = overflowLimit;
    }

    public BigInteger getUnderflowLimit() {
        return underflowLimit;
    }

    public void setUnderflowLimit(BigInteger underflowLimit) {
        this.underflowLimit = underflowLimit;
    }

    public long getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(long stackLimit) {
        this.stackLimit = stackLimit;
    }
}
