package com.hileco.cortex.instructions.bits;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.vm.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public abstract class BitInstruction implements Instruction {
    public static final StackParameter LEFT = new StackParameter("left", 0);
    public static final StackParameter RIGHT = new StackParameter("right", 1);

    abstract byte innerExecute(byte left, byte right);

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var left = stack.pop();
        var right = stack.pop();
        var result = new byte[Math.max(left.length, right.length)];

        for (var i = 0; i < result.length; i++) {
            var leftByte = i < left.length ? left[i] : 0;
            var rightByte = i < right.length ? right[i] : 0;
            result[i] = this.innerExecute(leftByte, rightByte);
        }
        stack.push(result);
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

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(LEFT, RIGHT);
    }
}
