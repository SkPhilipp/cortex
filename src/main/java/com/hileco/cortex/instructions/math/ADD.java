package com.hileco.cortex.instructions.math;

import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
public class ADD extends MathInstruction {
    @Override
    public BigInteger innerExecute(VirtualMachine process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.add(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
    }
}
