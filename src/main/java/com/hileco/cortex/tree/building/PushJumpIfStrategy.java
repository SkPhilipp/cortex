package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
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
            if (first.getOperation() instanceof Operations.Push
                    && first.getOperands() instanceof Operations.Push.Operands
                    && second.getOperation() instanceof Operations.JumpIf
                    && second.getOperands() instanceof Operations.JumpIf.Operands) {
                instructions.remove(i + 1);
                instructions.remove(i);
                Operations.Push.Operands pushData = (Operations.Push.Operands) first.getOperands();
                if (new BigInteger(pushData.bytes).compareTo(BigInteger.ZERO) > 0) {
                    Operations.JumpIf.Operands jumpData = (Operations.JumpIf.Operands) second.getOperands();
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
