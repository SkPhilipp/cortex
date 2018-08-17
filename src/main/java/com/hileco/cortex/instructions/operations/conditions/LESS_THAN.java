package com.hileco.cortex.instructions.operations.conditions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class LESS_THAN extends ConditionOperation {
    public boolean innerExecute(byte[] left, byte[] right) {
        BigInteger leftAsBigInteger = new BigInteger(left);
        BigInteger rightAsBigInteger = new BigInteger(right);
        return leftAsBigInteger.compareTo(rightAsBigInteger) < 0;
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }
}
