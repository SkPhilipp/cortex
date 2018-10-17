package com.hileco.cortex.instructions.stack;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;

@Value
public class PUSH implements Instruction {
    private byte[] bytes;

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        program.getStack().push(this.bytes);
        if (program.getStack().size() > process.getStackLimit()) {
            throw new ProgramException(program, STACK_LIMIT_REACHED);
        }
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
        return List.of();
    }

    @Override
    public String toString() {
        return String.format("PUSH %s", new BigInteger(this.bytes));
    }
}
