package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import java.util.stream.Collectors;

public class JumpUnreachableProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(treeNode -> treeNode.countEntries() == 0)
                .collect(Collectors.toList())
                .forEach(tree::remove);
    }
}
