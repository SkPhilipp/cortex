package com.hileco.cortex.instructions.stack;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.Value;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
public class DUPLICATE implements Instruction {
    private int topOffset;

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() <= this.topOffset) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        stack.duplicate(this.topOffset);
        if (stack.size() > process.getStackLimit()) {
            throw new ProgramException(program, STACK_LIMIT_REACHED);
        }
    }

    @Override
    public List<Integer> getStackTakes() {
        return Collections.singletonList(this.topOffset);
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
        return String.format("DUPLICATE %d", this.topOffset);
    }
}
