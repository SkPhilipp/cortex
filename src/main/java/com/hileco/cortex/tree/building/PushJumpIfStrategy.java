package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Instructions;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.math.BigInteger;
import java.util.List;

public class PushJumpIfStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i + 1 < instructions.size(); i++) {
            Instruction first = instructions.get(i);
            Instruction second = instructions.get(i + 1);
            if (first.getExecutor() instanceof Instructions.Push
                    && first.getData() instanceof Instructions.Push.Data
                    && second.getExecutor() instanceof Instructions.JumpIf
                    && second.getData() instanceof Instructions.JumpIf.Data) {
                instructions.remove(i + 1);
                instructions.remove(i);
                Instructions.Push.Data pushData = (Instructions.Push.Data) first.getData();
                if (new BigInteger(pushData.bytes).compareTo(BigInteger.ZERO) > 0) {
                    Instructions.JumpIf.Data jumpData = (Instructions.JumpIf.Data) second.getData();
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .JUMP(jumpData.destination)
                            .build());
                } else {
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .NOOP()
                            .build());
                }
            }
        }
    }
}
