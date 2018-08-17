package com.hileco.cortex.instructions.operations.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;

import java.math.BigInteger;

public class SUBTRACT extends MathOperation {
    public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.subtract(right).mod(process.getUnderflowLimit().subtract(BigInteger.ONE));
    }
}
