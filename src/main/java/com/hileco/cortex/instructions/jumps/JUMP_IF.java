package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
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

    public static final StackParameter ADDRESS = new StackParameter("address", 0);
    public static final StackParameter CONDITION = new StackParameter("condition", 1);

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
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, INSTRUCTION_POSITION);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(ADDRESS, CONDITION);
    }

    @Override
    public String toString() {
        return "JUMP_IF";
    }
}
