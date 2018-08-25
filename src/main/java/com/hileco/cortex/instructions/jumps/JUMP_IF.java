package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class JUMP_IF extends JumpingInstruction {
    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var nextInstructionPosition = new BigInteger(program.getStack().pop()).intValue();
        var top = stack.pop();
        var isNonZero = false;
        for (var item : top) {
            if (item > 0) {
                isNonZero = true;
            }
        }
        if (isNonZero) {
            this.performJump(program, nextInstructionPosition);
        }
    }

    @Override
    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, INSTRUCTION_POSITION);
    }

    @Override
    public String toString() {
        return "JUMP_IF";
    }
}
