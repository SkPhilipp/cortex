package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class JUMP extends JumpingInstruction {
    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 1) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        int nextInstructionPosition = new BigInteger(stack.pop()).intValue();
        this.performJump(program, nextInstructionPosition);
    }

    @Override
    public List<Integer> getStackTakes() {
        return Collections.singletonList(0);
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(INSTRUCTION_POSITION);
    }

    @Override
    public String toString() {
        return "JUMP";
    }
}
