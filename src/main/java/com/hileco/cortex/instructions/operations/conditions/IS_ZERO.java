package com.hileco.cortex.instructions.operations.conditions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.operations.Operation;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;

public class IS_ZERO implements Operation {
    public void execute(ProcessContext process, ProgramContext program) {
        LayeredStack<byte[]> stack = program.getStack();
        byte[] top = stack.pop();
        boolean isZero = true;
        for (byte item : top) {
            if (item > 0) {
                isZero = false;
            }
        }
        byte[] resultReference = isZero ? ConditionOperation.TRUE : ConditionOperation.FALSE;
        stack.push(resultReference.clone());
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
