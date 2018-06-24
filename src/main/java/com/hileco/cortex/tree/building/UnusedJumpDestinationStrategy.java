package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Instructions;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnusedJumpDestinationStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        Set<Integer> jumpedDestinations = new HashSet<>();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            if (instruction.getExecutor() instanceof Instructions.Jump
                    && instruction.getData() instanceof Instructions.Jump.Data) {
                Instructions.Jump.Data data = (Instructions.Jump.Data) instruction.getData();
                jumpedDestinations.add(data.destination);
            }
            if (instruction.getExecutor() instanceof Instructions.JumpIf
                    && instruction.getData() instanceof Instructions.JumpIf.Data) {
                Instructions.JumpIf.Data data = (Instructions.JumpIf.Data) instruction.getData();
                jumpedDestinations.add(data.destination);
            }
        }
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            if (instruction.getExecutor() instanceof Instructions.JumpDestination) {
                if (!jumpedDestinations.contains(i)) {
                    instructions.remove(i);
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .build());
                }
            }
        }
    }
}
