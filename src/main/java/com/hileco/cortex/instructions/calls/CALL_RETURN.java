package com.hileco.cortex.instructions.calls;

import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.RETURN_DATA_TOO_LARGE;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public class CALL_RETURN implements Instruction {

    public static final StackParameter OFFSET = new StackParameter("size", 0);
    public static final StackParameter SIZE = new StackParameter("offset", 1);

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var offset = new BigInteger(stack.pop());
        var size = new BigInteger(stack.pop());
        process.getPrograms().pop();
        var nextContext = process.getPrograms().peek();
        var data = program.getMemory().read(offset.intValue(), size.intValue());
        var wSize = nextContext.getReturnDataSize();
        if (data.length > wSize.intValue()) {
            throw new ProgramException(program, RETURN_DATA_TOO_LARGE);
        }
        var dataExpanded = Arrays.copyOf(data, wSize.intValue());
        var wOffset = nextContext.getReturnDataOffset();
        nextContext.getMemory().write(wOffset.intValue(), dataExpanded, wSize.intValue());
    }

    @Override
    public List<Integer> getStackAdds() {
        return List.of();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return List.of(STACK, PROGRAM_CONTEXT, MEMORY);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(OFFSET, SIZE);
    }

    @Override
    public String toString() {
        return "CALL_RETURN";
    }
}
