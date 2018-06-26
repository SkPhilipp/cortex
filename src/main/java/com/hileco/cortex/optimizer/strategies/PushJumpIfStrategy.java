package com.hileco.cortex.optimizer.strategies;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.optimizer.InstructionsOptimizeStrategy;

import java.math.BigInteger;
import java.util.List;

public class PushJumpIfStrategy implements InstructionsOptimizeStrategy {
    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i + 2 < instructions.size(); i++) {
            Instruction first = instructions.get(i);
            Instruction second = instructions.get(i + 1);
            Instruction third = instructions.get(i + 2);
            if (first.getOperation() instanceof Operations.Push
                    && first.getOperands() instanceof Operations.Push.Operands
                    && second.getOperation() instanceof Operations.Push
                    && second.getOperands() instanceof Operations.Push.Operands
                    && third.getOperation() instanceof Operations.JumpIf) {
                instructions.remove(i + 2);
                instructions.remove(i + 1);
                instructions.remove(i);
                Operations.Push.Operands conditionPushData = (Operations.Push.Operands) first.getOperands();
                Operations.Push.Operands destinationPushData = (Operations.Push.Operands) second.getOperands();
                if (new BigInteger(conditionPushData.bytes).compareTo(BigInteger.ZERO) > 0) {
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .PUSH(destinationPushData.bytes)
                            .JUMP()
                            .build());
                } else {
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .NOOP()
                            .NOOP()
                            .build());
                }
            }
        }
    }
}
