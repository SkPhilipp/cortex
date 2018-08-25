package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

public class ExitTrimProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(treeNode -> treeNode.countExits() == 0)
                // TODO: Trim dead code in replacement
                .forEach(treeBlock -> tree.replace(treeBlock, treeBlock));
    }
}
