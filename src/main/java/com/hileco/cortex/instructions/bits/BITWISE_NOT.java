package com.hileco.cortex.instructions.bits;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;

public class BITWISE_NOT implements Instruction {
    public void execute(ProcessContext process, ProgramContext program) {
        LayeredStack<byte[]> stack = program.getStack();
        byte[] pop = stack.pop();
        byte[] result = new byte[pop.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = 127;
            result[i] ^= pop[i];
        }
        stack.push(result);
    }

    public List<Integer> getStackTakes() {
        return Collections.singletonList(0);
    }

    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(STACK);
    }
}
