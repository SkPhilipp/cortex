package com.hileco.cortex.instructions.operations.stack;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.operations.Operation;
import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
public class SWAP implements Operation {
    private int topOffsetLeft;
    private int topOffsetRight;

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() <= topOffsetLeft || stack.size() <= topOffsetRight) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        stack.swap(topOffsetLeft, topOffsetRight);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(topOffsetLeft, topOffsetRight);
    }

    public List<Integer> getStackAdds() {
        return Arrays.asList(topOffsetRight, topOffsetLeft);
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    public String toString() {
        return String.format("SWAP %d %d", topOffsetLeft, topOffsetRight);
    }
}
