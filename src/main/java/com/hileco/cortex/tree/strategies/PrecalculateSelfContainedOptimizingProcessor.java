package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;

import java.util.Collections;

import static com.hileco.cortex.tree.stream.Filters.isSelfContained;

public class PrecalculateSelfContainedOptimizingProcessor implements ProgramTreeProcessor {

    @Override
    public void process(ProgramTree programTree) {
        // TODO: Does not properly account for SWAP, DUP, and JUMP_DESTINATION
        programTree.getNodes()
                .stream()
                .filter(isSelfContained())
                .forEach(programNode -> {
                    ProgramTree tree = new ProgramTree(Collections.singletonList(programNode));
                    System.out.println(tree);
                });
    }
}
