package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.List;

public class UnusedJumpDestinationStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
// TODO: Broken since JUMP and JUMP_IF taking a parameter
//        Set<Integer> jumpedDestinations = new HashSet<>();
//        for (int i = 0; i < instructions.size(); i++) {
//            Instruction instruction = instructions.get(i);
//            if (instruction.getOperation() instanceof Operations.Jump
//                    && instruction.getOperands() instanceof Operations.Jump.Operands) {
//                Operations.Jump.Operands data = (Operations.Jump.Operands) instruction.getOperands();
//                jumpedDestinations.add(data.destination);
//            }
//            if (instruction.getOperation() instanceof Operations.JumpIf
//                    && instruction.getOperands() instanceof Operations.JumpIf.Operands) {
//                Operations.JumpIf.Operands data = (Operations.JumpIf.Operands) instruction.getOperands();
//                jumpedDestinations.add(data.destination);
//            }
//        }
//        for (int i = 0; i < instructions.size(); i++) {
//            Instruction instruction = instructions.get(i);
//            if (instruction.getOperation() instanceof Operations.JumpDestination) {
//                if (!jumpedDestinations.contains(i)) {
//                    instructions.remove(i);
//                    instructions.addAll(i, programBuilderFactory.builder()
//                            .NOOP()
//                            .build());
//                }
//            }
//        }
    }
}
