package com.hileco.cortex.instructions.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
public class ADD extends MathInstruction {
    public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.add(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
    }
}
