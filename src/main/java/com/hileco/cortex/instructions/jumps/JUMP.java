package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class JUMP extends JumpingInstruction {

    public static final StackParameter ADDRESS = new StackParameter("address", 0);

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 1) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var nextInstructionPosition = new BigInteger(stack.pop()).intValue();
        this.performJump(program, nextInstructionPosition);
    }

    @Override
    public List<Integer> getStackAdds() {
        return List.of();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(INSTRUCTION_POSITION);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(ADDRESS);
    }

    @Override
    public String toString() {
        return "JUMP";
    }
}
