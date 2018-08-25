package com.hileco.cortex.instructions.conditions;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
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
}
