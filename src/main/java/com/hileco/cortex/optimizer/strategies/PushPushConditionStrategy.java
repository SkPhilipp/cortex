package com.hileco.cortex.optimizer.strategies;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.instructions.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.optimizer.InstructionsOptimizeStrategy;

import java.util.List;

public class PushPushConditionStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i + 2 < instructions.size(); i++) {
            Instruction first = instructions.get(i);
            Instruction second = instructions.get(i + 1);
            Instruction third = instructions.get(i + 2);
            if (first.getOperation() instanceof Operations.Push
                    && second.getOperation() instanceof Operations.Push
                    && (third.getOperation() instanceof Operations.Equals
                    || third.getOperation() instanceof Operations.GreaterThan
                    || third.getOperation() instanceof Operations.LessThan
                    || third.getOperation() instanceof Operations.IsZero)) {
                ProgramContext programContext = new ProgramContext();
                ProgramRunner programRunner = new ProgramRunner(programContext);
                List<Instruction> sublist = instructions.subList(i, i + 1 + 2);
                try {
                    programRunner.run(sublist);
                    byte[] bytes = programContext.getStack().pop();
                    instructions.remove(i + 2);
                    instructions.remove(i + 1);
                    instructions.remove(i);
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .NOOP()
                            .PUSH(bytes)
                            .build());
                } catch (ProgramException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}