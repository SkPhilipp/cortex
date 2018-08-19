package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.instructions.debug.NOOP;

import static com.hileco.cortex.analysis.Predicates.instruction;
import static com.hileco.cortex.analysis.Predicates.type;

public class NoopProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(NOOP.class)))
                .forEach(treeBlock::remove));
    }
}
