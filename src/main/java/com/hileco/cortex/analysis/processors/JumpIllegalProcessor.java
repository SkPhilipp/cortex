package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

import static com.hileco.cortex.analysis.Predicates.invalidExit;

public class JumpIllegalProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().stream()
                .filter(invalidExit(tree))
                // TODO: replace the specific illegal exit with a HALT
                .forEach(entry -> tree.replace(entry, entry));
    }
}
