package com.hileco.cortex.instructions.conditions;

import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class LESS_THAN extends ConditionInstruction {
    public boolean innerExecute(byte[] left, byte[] right) {
        BigInteger leftAsBigInteger = new BigInteger(left);
        BigInteger rightAsBigInteger = new BigInteger(right);
        return leftAsBigInteger.compareTo(rightAsBigInteger) < 0;
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }
}
