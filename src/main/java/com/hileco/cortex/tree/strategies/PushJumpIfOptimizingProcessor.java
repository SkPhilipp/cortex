package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import lombok.Value;

import java.math.BigInteger;

import static com.hileco.cortex.tree.stream.Filters.instruction;
import static com.hileco.cortex.tree.stream.Filters.parameter;

@Value
public class PushJumpIfOptimizingProcessor implements ProgramTreeProcessor {

    @Override
    public void process(ProgramTree programTree) {
        programTree.getNodes()
                .stream()
                .filter(instruction(JUMP_IF.class))
                .filter(parameter(0, instruction(PUSH.class)))
                .filter(parameter(1, instruction(PUSH.class)))
                .forEach(programNode -> {
                    ProgramNode parameterDestinationNode = programNode.getParameters().get(0);
                    ProgramNode parameterConditionNode = programNode.getParameters().get(1);
                    PUSH push = (PUSH) parameterConditionNode.getInstruction();
                    if (new BigInteger(push.getBytes()).compareTo(BigInteger.ZERO) > 0) {
                        // TODO: Detatch the NOOP parameter
                        parameterConditionNode.setInstruction(new NOOP());
                        programNode.setInstruction(new JUMP());
                    } else {
                        // TODO: Detatch all NOOP parameters
                        parameterConditionNode.setInstruction(new NOOP());
                        parameterDestinationNode.setInstruction(new NOOP());
                        programNode.setInstruction(new NOOP());
                    }
                });
    }
}
