package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;

import static com.hileco.cortex.instructions.Operations.JumpDestination;
import static com.hileco.cortex.tree.ProgramNodeType.INSTRUCTION;

public class JumpDestinationProcessor implements ProgramTreeProcessor {
    @Override
    public void process(ProgramTree programTree) {
        programTree.getNodes().forEach(programNode -> {
            if (programNode.getType() == INSTRUCTION) {
                if (programNode.getInstruction().getOperation() instanceof JumpDestination) {
                    // TODO: Find paths to this JUMP_DESTINATION
                }
            }
        });
    }
}
