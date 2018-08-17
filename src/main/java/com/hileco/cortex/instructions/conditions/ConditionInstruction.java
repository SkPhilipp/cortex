package com.hileco.cortex.instructions.conditions;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.Instruction;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

abstract class ConditionInstruction implements Instruction {
    static final byte[] TRUE = {1};
    static final byte[] FALSE = {0};

    abstract boolean innerExecute(byte[] left, byte[] right);

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        byte[] left = stack.pop();
        byte[] right = stack.pop();
        boolean equals = innerExecute(left, right);
        stack.push(equals ? TRUE.clone() : FALSE.clone());
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
