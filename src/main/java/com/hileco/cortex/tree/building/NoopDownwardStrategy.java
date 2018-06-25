package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.List;

public class NoopDownwardStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
// TODO: Broken since JUMP and JUMP_IF taking a parameter
//        Map<Integer, List<Consumer<Integer>>> updateable = new HashMap<>();
//        int removed = 0;
//        int initialSize = instructions.size();
//        for (int i = 0; i < initialSize; i++) {
//            Instruction instruction = instructions.get(i - removed);
//            if (instruction.getOperation() instanceof Operations.Jump
//                    && instruction.getOperands() instanceof Operations.Jump.Operands) {
//                Operations.Jump.Operands data = (Operations.Jump.Operands) instruction.getOperands();
//                List<Consumer<Integer>> consumers = updateable.computeIfAbsent(data.destination, k -> new ArrayList<>());
//                consumers.add((update) -> data.destination = update);
//            }
//            if (instruction.getOperation() instanceof Operations.JumpIf
//                    && instruction.getOperands() instanceof Operations.JumpIf.Operands) {
//                Operations.JumpIf.Operands data = (Operations.JumpIf.Operands) instruction.getOperands();
//                List<Consumer<Integer>> consumers = updateable.computeIfAbsent(data.destination, k -> new ArrayList<>());
//                consumers.add((update) -> data.destination = update);
//            }
//            if (instruction.getOperation() instanceof Operations.NoOp) {
//                instructions.remove(i - removed);
//                removed++;
//            }
//            List<Consumer<Integer>> consumers = updateable.get(i);
//            if (consumers != null) {
//                int newDestination = i - removed;
//                consumers.forEach(consumer -> consumer.accept(newDestination));
//            }
//        }
    }
}
