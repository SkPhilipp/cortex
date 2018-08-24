package com.hileco.cortex.instructions.conditions;

import java.util.Arrays;
import java.util.List;

public class EQUALS extends ConditionInstruction {
    public boolean innerExecute(byte[] left, byte[] right) {
        return Arrays.equals(left, right);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }
}
