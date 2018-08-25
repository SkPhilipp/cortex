package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import java.util.stream.Collectors;

public class JumpThreadingProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(treeNode -> treeNode.countEntries() <= 1)
                .collect(Collectors.toList())
                .forEach(tree::mergeUpwards);
        tree.getTreeBlocks().stream()
                .filter(treeNode -> treeNode.countExits() <= 1)
                .collect(Collectors.toList())
                .forEach(tree::mergeDownwards);
    }
}
