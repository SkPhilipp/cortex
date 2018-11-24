package com.hileco.cortex.instructions.stack;


import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.vm.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

public class DUPLICATE implements Instruction {
    private final StackParameter input;

    public DUPLICATE(int topOffset) {
        this.input = new StackParameter("input", topOffset);
    }

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() <= this.input.getPosition()) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        stack.duplicate(this.input.getPosition());
        if (stack.size() > process.getStackLimit()) {
            throw new ProgramException(program, STACK_LIMIT_REACHED);
        }
    }

    public int getPosition() {
        return this.input.getPosition();
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
    public List<StackParameter> getStackParameters() {
        return List.of(this.input);
    }

    @Override
    public String toString() {
        return String.format("DUPLICATE %d", this.input.getPosition());
    }
}
