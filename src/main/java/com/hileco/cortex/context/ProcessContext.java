package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredStack;
import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProcessContext {

    public static final BigInteger NUMERICAL_LIMIT = new BigInteger(new byte[]{2}).pow(256).subtract(BigInteger.ONE);

    private LayeredStack<ProgramContext> programs;
    private Map<BigInteger, Program> atlas;
    private BigInteger overflowLimit;
    private BigInteger underflowLimit;
    private long stackLimit;

    public ProcessContext(ProgramContext programContext) {
        programs = new LayeredStack<>();
        programs.push(programContext);
        atlas = new HashMap<>();
        stackLimit = Long.MAX_VALUE;
        overflowLimit = NUMERICAL_LIMIT;
        underflowLimit = NUMERICAL_LIMIT;
    }
}
