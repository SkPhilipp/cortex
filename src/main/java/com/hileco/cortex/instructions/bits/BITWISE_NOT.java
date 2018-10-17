package com.hileco.cortex.instructions.bits;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;

@EqualsAndHashCode
public class BITWISE_NOT implements Instruction {

    public static final StackParameter INPUT = new StackParameter("input", 0);

    private static final byte CLEAR = 127;

    @Override
    public void execute(ProcessContext process, ProgramContext program) {
        var stack = program.getStack();
        var pop = stack.pop();
        var result = new byte[pop.length];
        for (var i = 0; i < result.length; i++) {
            result[i] = CLEAR;
            result[i] ^= pop[i];
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
    public List<StackParameter> getStackParameters() {
        return List.of(INPUT);
    }

    @Override
    public String toString() {
        return "BITWISE_NOT";
    }
}
