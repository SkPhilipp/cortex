package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import java.util.stream.Collectors;

import static com.hileco.cortex.analysis.Predicates.maximumEntries;
import static com.hileco.cortex.analysis.Predicates.maximumExits;

public class JumpThreadingProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(maximumEntries(1))
                .collect(Collectors.toList())
                .forEach(tree::mergeUpwards);
        tree.getTreeBlocks().stream()
                .filter(maximumExits(1))
                .collect(Collectors.toList())
                .forEach(tree::mergeDownwards);
    }
}
