package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuildingStrategy;

import static com.hileco.cortex.instructions.Operations.JumpDestination;
import static com.hileco.cortex.tree.ProgramNodeType.INSTRUCTION;
import static com.hileco.cortex.tree.ProgramNodeType.JUMP_DESTINATION;

public class JumpDestinationStrategy implements ProgramTreeBuildingStrategy {
    @Override
    public void expand(ProgramTree programTree) {
        programTree.getNodes().forEach(programNode -> {
            if(programNode.getType() == INSTRUCTION) {
                if(programNode.getInstruction().getOperation() instanceof JumpDestination) {
                    programNode.setType(JUMP_DESTINATION);
                }
            }
        });
    }
}
