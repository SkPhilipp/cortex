package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Instructions;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NoopDownwardStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        Map<Integer, List<Consumer<Integer>>> updateable = new HashMap<>();
        int removed = 0;
        int initialSize = instructions.size();
        for (int i = 0; i < initialSize; i++) {
            Instruction instruction = instructions.get(i - removed);
            if (instruction.getExecutor() instanceof Instructions.Jump
                    && instruction.getData() instanceof Instructions.Jump.Data) {
                Instructions.Jump.Data data = (Instructions.Jump.Data) instruction.getData();
                List<Consumer<Integer>> consumers = updateable.computeIfAbsent(data.destination, k -> new ArrayList<>());
                consumers.add((update) -> data.destination = update);
            }
            if (instruction.getExecutor() instanceof Instructions.JumpIf
                    && instruction.getData() instanceof Instructions.JumpIf.Data) {
                Instructions.JumpIf.Data data = (Instructions.JumpIf.Data) instruction.getData();
                List<Consumer<Integer>> consumers = updateable.computeIfAbsent(data.destination, k -> new ArrayList<>());
                consumers.add((update) -> data.destination = update);
            }
            if (instruction.getExecutor() instanceof Instructions.NoOp) {
                instructions.remove(i - removed);
                removed++;
            }
            List<Consumer<Integer>> consumers = updateable.get(i);
            if (consumers != null) {
                int newDestination = i - removed;
                consumers.forEach(consumer -> consumer.accept(newDestination));
            }
        }
    }
}
