package com.hileco.cortex.instructions.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import lombok.Value;

import java.math.BigInteger;

@Value
public class MULTIPLY extends MathInstruction {
    public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.multiply(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
    }
}