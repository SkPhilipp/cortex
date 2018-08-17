package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import lombok.Value;

import java.math.BigInteger;

import static com.hileco.cortex.instructions.Operations.JumpIf;
import static com.hileco.cortex.instructions.Operations.Push;
import static com.hileco.cortex.tree.stream.Filters.operation;
import static com.hileco.cortex.tree.stream.Filters.parameter;

@Value
public class PushJumpIfOptimizingProcessor implements ProgramTreeProcessor {

    private final ProgramBuilderFactory programBuilderFactory;

    @Override
    public void process(ProgramTree programTree) {
        programTree.getNodes()
                .stream()
                .filter(operation(JumpIf.class))
                .filter(parameter(0, operation(Push.class)))
                .filter(parameter(1, operation(Push.class)))
                .forEach(programNode -> {
                    ProgramNode parameterDestinationNode = programNode.getParameters().get(0);
                    ProgramNode parameterConditionNode = programNode.getParameters().get(1);
                    Push.Operands pushOperands = (Push.Operands) parameterConditionNode.getInstruction().getOperands();
                    ProgramBuilder builder = programBuilderFactory.builder();
                    if (new BigInteger(pushOperands.bytes).compareTo(BigInteger.ZERO) > 0) {
                        // TODO: Detatch the NOOP parameter
                        parameterConditionNode.setInstruction(builder.NOOP().get());
                        programNode.setInstruction(builder.JUMP().get());
                    } else {
                        // TODO: Detatch all NOOP parameters
                        parameterConditionNode.setInstruction(builder.NOOP().get());
                        parameterDestinationNode.setInstruction(builder.NOOP().get());
                        programNode.setInstruction(builder.NOOP().get());
                    }
                });
    }
}
