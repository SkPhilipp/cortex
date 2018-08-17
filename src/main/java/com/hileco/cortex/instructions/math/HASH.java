package com.hileco.cortex.instructions.math;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.Instruction;
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

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashMethod);
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 1) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            messageDigest.update(stack.pop());
            stack.push(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(String.format("Unknown hash method: %s", hashMethod), e);
        }
    }

    public List<Integer> getStackTakes() {
        return Collections.singletonList(0);
    }

    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    public String toString() {
        return String.format("HASH %s", hashMethod);
    }
}
