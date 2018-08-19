package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import static com.hileco.cortex.analysis.Predicates.maximumExits;

public class ExitTrimProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(maximumExits(0))
                // TODO: trim dead code in replacement
                .forEach(treeBlock -> tree.replace(treeBlock, treeBlock));
    }
}
