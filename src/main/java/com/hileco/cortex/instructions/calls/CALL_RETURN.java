package com.hileco.cortex.instructions.calls;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.RETURN_DATA_TOO_LARGE;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public class CALL_RETURN implements Instruction {
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        BigInteger offset = new BigInteger(stack.pop());
        BigInteger size = new BigInteger(stack.pop());
        process.getPrograms().pop();
        ProgramContext nextContext = process.getPrograms().peek();
        byte[] data = program.getMemory().read(offset.intValue(), size.intValue());
        BigInteger wSize = nextContext.getReturnDataSize();
        if (data.length > wSize.intValue()) {
            throw new ProgramException(program, RETURN_DATA_TOO_LARGE);
        }
        byte[] dataExpanded = Arrays.copyOf(data, wSize.intValue());
        BigInteger wOffset = nextContext.getReturnDataOffset();
        nextContext.getMemory().write(wOffset.intValue(), dataExpanded, wSize.intValue());
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, PROGRAM_CONTEXT, MEMORY);
    }

    public String toString() {
        return "CALL_RETURN";
    }
}
