package com.hileco.cortex.instructions.conditions;

import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class EQUALS extends ConditionInstruction {
    @Override
    public boolean innerExecute(byte[] left, byte[] right) {
        return Arrays.equals(left, right);
    }

    @Override
    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }
}
