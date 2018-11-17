package com.hileco.cortex.instructions.math;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.vm.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
public class HASH implements Instruction {

    public static final StackParameter INPUT = new StackParameter("input", 0);

    private String hashMethod;

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
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
    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(INPUT);
    }

    @Override
    public String toString() {
        return String.format("HASH %s", this.hashMethod);
    }
}
