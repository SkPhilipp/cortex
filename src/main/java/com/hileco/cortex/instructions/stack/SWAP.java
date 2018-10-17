package com.hileco.cortex.instructions.stack;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

public class SWAP implements Instruction {
    private final StackParameter left;
    private final StackParameter right;

    public SWAP(int topOffsetLeft, int topOffsetRight) {
        this.left = new StackParameter("left", topOffsetLeft);
        this.right = new StackParameter("right", topOffsetRight);
    }

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() <= this.left.getPosition() || stack.size() <= this.right.getPosition()) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        stack.swap(this.left.getPosition(), this.right.getPosition());
    }

    @Override
    public List<Integer> getStackAdds() {
        return List.of(this.right.getPosition(), this.left.getPosition());
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(this.left, this.right);
    }

    @Override
    public String toString() {
        return String.format("SWAP %d %d", this.left.getPosition(), this.right.getPosition());
    }
}
