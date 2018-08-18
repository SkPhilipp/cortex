package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Predicates;
import com.hileco.cortex.analysis.Tree;

public class KnownProcessor implements Processor {
    public void process(Tree tree) {
        // TODO: Thorough testing (with Fuzzer, could use a some kind of dynamic elimination protocol to find erring processor)
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(Predicates.selfContained())
                .forEach(treeNode -> {
                    // TODO: Implement
                }));
    }
}
