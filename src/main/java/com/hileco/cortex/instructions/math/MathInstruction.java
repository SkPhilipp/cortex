package com.hileco.cortex.instructions.math;


import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.vm.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
abstract class MathInstruction implements Instruction {
    public static final StackParameter LEFT = new StackParameter("left", 0);
    public static final StackParameter RIGHT = new StackParameter("right", 1);

    public abstract BigInteger innerExecute(VirtualMachine process, ProgramContext program, BigInteger left, BigInteger right);

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var left = stack.pop();
        var right = stack.pop();
        var leftAsBigInteger = new BigInteger(left);
        var rightAsBigInteger = new BigInteger(right);
        var result = this.innerExecute(process, program, leftAsBigInteger, rightAsBigInteger);
        stack.push(result.toByteArray());
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
        return List.of(LEFT, RIGHT);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}