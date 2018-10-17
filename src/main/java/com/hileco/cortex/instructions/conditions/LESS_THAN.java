package com.hileco.cortex.instructions.conditions;

import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
public class LESS_THAN extends ConditionInstruction {
    @Override
    public boolean innerExecute(byte[] left, byte[] right) {
        var leftAsBigInteger = new BigInteger(left);
        var rightAsBigInteger = new BigInteger(right);
        return leftAsBigInteger.compareTo(rightAsBigInteger) < 0;
    }
}
