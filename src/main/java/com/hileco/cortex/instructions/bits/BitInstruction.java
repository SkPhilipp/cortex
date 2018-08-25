package com.hileco.cortex.instructions.bits;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public abstract class BitInstruction implements Instruction {
    abstract byte innerExecute(byte left, byte right);

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var left = stack.pop();
        var right = stack.pop();
        var result = new byte[Math.max(left.length, right.length)];

        for (var i = 0; i < result.length; i++) {
            byte leftByte = i < left.length ? left[i] : 0;
            byte rightByte = i < right.length ? right[i] : 0;
            result[i] = this.innerExecute(leftByte, rightByte);
        }
        stack.push(result);
    }

    @Override
    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
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
        return this.getClass().getSimpleName();
    }
}
