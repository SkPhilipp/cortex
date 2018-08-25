package com.hileco.cortex.instructions.stack;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
public class SWAP implements Instruction {
    private int topOffsetLeft;
    private int topOffsetRight;

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() <= this.topOffsetLeft || stack.size() <= this.topOffsetRight) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        stack.swap(this.topOffsetLeft, this.topOffsetRight);
    }

    @Override
    public List<Integer> getStackTakes() {
        return Arrays.asList(this.topOffsetLeft, this.topOffsetRight);
    }

    @Override
    public List<Integer> getStackAdds() {
        return Arrays.asList(this.topOffsetRight, this.topOffsetLeft);
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    @Override
    public String toString() {
        return String.format("SWAP %d %d", this.topOffsetLeft, this.topOffsetRight);
    }
}
