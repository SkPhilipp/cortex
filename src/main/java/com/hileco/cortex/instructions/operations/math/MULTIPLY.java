package com.hileco.cortex.instructions.operations.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;

import java.math.BigInteger;

public class MULTIPLY extends MathOperation {
    public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.multiply(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
    }
}
