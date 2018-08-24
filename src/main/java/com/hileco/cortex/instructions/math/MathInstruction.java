package com.hileco.cortex.instructions.math;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
abstract class MathInstruction implements Instruction {
    public abstract BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right);

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        byte[] left = stack.pop();
        byte[] right = stack.pop();
        BigInteger leftAsBigInteger = new BigInteger(left);
        BigInteger rightAsBigInteger = new BigInteger(right);
        BigInteger result = innerExecute(process, program, leftAsBigInteger, rightAsBigInteger);
        stack.push(result.toByteArray());
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}