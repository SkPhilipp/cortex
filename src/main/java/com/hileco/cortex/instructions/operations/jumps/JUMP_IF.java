package com.hileco.cortex.instructions.operations.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

public class JUMP_IF extends JumpingOperation {
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        int nextInstructionPosition = new BigInteger(program.getStack().pop()).intValue();
        byte[] top = stack.pop();
        boolean isNonZero = false;
        for (byte item : top) {
            if (item > 0) {
                isNonZero = true;
            }
        }
        if (isNonZero) {
            performJump(program, nextInstructionPosition);
        }
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, INSTRUCTION_POSITION);
    }

    public String toString() {
        return "JUMP_IF";
    }
}
