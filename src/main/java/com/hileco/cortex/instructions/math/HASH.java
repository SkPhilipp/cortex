package com.hileco.cortex.instructions.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
public class HASH implements Instruction {
    private String hashMethod;

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        try {
            var messageDigest = MessageDigest.getInstance(this.hashMethod);
            var stack = program.getStack();
            if (stack.size() < 1) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            messageDigest.update(stack.pop());
            stack.push(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(String.format("Unknown hash method: %s", this.hashMethod), e);
        }
    }

    @Override
    public List<Integer> getStackTakes() {
        return Collections.singletonList(0);
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
        return String.format("HASH %s", this.hashMethod);
    }
}
