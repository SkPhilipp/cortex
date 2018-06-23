package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Instructions;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.List;

public class PushPopStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i + 1 < instructions.size(); i++) {
            Instruction first = instructions.get(i);
            Instruction second = instructions.get(i + 1);
            if (first.getExecutor() instanceof Instructions.Push
                    && second.getExecutor() instanceof Instructions.Pop) {
                instructions.remove(i + 1);
                instructions.remove(i);
                instructions.addAll(i, programBuilderFactory
                        .builder()
                        .NOOP()
                        .NOOP()
                        .build());
            }
        }
    }
}
