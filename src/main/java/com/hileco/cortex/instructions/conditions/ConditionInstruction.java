package com.hileco.cortex.instructions.conditions;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
abstract class ConditionInstruction implements Instruction {
    static final byte[] TRUE = {1};
    static final byte[] FALSE = {0};

    abstract boolean innerExecute(byte[] left, byte[] right);

    public static final StackParameter LEFT = new StackParameter("left", 0);
    public static final StackParameter RIGHT = new StackParameter("right", 1);

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var left = stack.pop();
        var right = stack.pop();
        var equals = this.innerExecute(left, right);
        stack.push(equals ? TRUE.clone() : FALSE.clone());
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(LEFT, RIGHT);
    }
}
