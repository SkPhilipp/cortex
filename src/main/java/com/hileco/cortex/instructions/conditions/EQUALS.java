package com.hileco.cortex.instructions.conditions;

import lombok.EqualsAndHashCode;

import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
public class EQUALS extends ConditionInstruction {
    @Override
    public boolean innerExecute(byte[] left, byte[] right) {
        return Arrays.equals(left, right);
    }
}
