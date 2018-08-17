package com.hileco.cortex.instructions.operations.bits;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.operations.Operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

public abstract class BitOperation implements Operation {
    abstract byte innerExecute(byte left, byte right);

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        byte[] left = stack.pop();
        byte[] right = stack.pop();
        byte[] result = new byte[Math.max(left.length, right.length)];

        for (int i = 0; i < result.length; i++) {
            byte leftByte = i < left.length ? left[i] : 0;
            byte rightByte = i < right.length ? right[i] : 0;
            result[i] = innerExecute(leftByte, rightByte);
        }
        stack.push(result);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
