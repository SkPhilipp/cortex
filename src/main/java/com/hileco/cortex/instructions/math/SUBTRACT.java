package com.hileco.cortex.instructions.math;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
public class SUBTRACT extends MathInstruction {
    @Override
    public BigInteger innerExecute(VirtualMachine process, ProgramContext program, BigInteger left, BigInteger right) {
        return left.subtract(right).mod(process.getUnderflowLimit().subtract(BigInteger.ONE));
    }
}
