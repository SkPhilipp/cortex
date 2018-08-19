package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import java.util.stream.Collectors;

import static com.hileco.cortex.analysis.Predicates.maximumEntries;

public class JumpUnreachableProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(maximumEntries(0))
                .collect(Collectors.toList())
                .forEach(tree::remove);
    }
}
