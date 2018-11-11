package com.hileco.cortex.instructions.conditions;

import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.STACK;

@EqualsAndHashCode
public class IS_ZERO implements Instruction {

    public static final StackParameter INPUT = new StackParameter("input", 0);

    @Override
    public void execute(VirtualMachine process, ProgramContext program) {
        var stack = program.getStack();
        var top = stack.pop();
        var isZero = true;
        for (var item : top) {
            if (item > 0) {
                isZero = false;
            }
        }
        var resultReference = isZero ? ConditionInstruction.TRUE : ConditionInstruction.FALSE;
        stack.push(resultReference.clone());
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
        return "IS_ZERO";
    }
}
