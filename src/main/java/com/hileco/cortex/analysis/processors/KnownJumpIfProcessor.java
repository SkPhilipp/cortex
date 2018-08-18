package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.instructions.jumps.JUMP_IF;

import static com.hileco.cortex.analysis.Predicates.instruction;
import static com.hileco.cortex.analysis.Predicates.parameter;
import static com.hileco.cortex.analysis.Predicates.selfContained;
import static com.hileco.cortex.analysis.Predicates.type;

public class KnownJumpIfProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(JUMP_IF.class)))
                .filter(parameter(1, selfContained()))
                // TODO: replace with NOOP | PUSH JUMP equivalent
                .forEach(treeNode -> treeBlock.replace(treeNode, treeNode)));
    }
}
