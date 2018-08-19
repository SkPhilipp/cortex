package com.hileco.cortex.instructions.conditions;

import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
public class EQUALS extends ConditionInstruction {
    public boolean innerExecute(byte[] left, byte[] right) {
        return Arrays.equals(left, right);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }
}
