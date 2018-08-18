package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.jumps.JUMP_IF;

import static com.hileco.cortex.analysis.Predicates.constraint;
import static com.hileco.cortex.analysis.Predicates.equalsToStatic;
import static com.hileco.cortex.analysis.Predicates.hashOfUnknown;
import static com.hileco.cortex.analysis.Predicates.instruction;
import static com.hileco.cortex.analysis.Predicates.invalidExit;
import static com.hileco.cortex.analysis.Predicates.maximumEntries;
import static com.hileco.cortex.analysis.Predicates.maximumExits;
import static com.hileco.cortex.analysis.Predicates.parameters;
import static com.hileco.cortex.analysis.Predicates.selfContained;
import static com.hileco.cortex.analysis.Predicates.type;

public class Optimizations {
    public void process(Tree tree) {

        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(LOAD.class)))
                .filter(parameters(selfContained()))
                // TODO: replace with PUSH equivalent
                .forEach(treeNode -> treeBlock.replace(treeNode, treeNode)));

        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(parameters(constraint(equalsToStatic())))
                // TODO: replace the matched parameter with a PUSH equivalent
                .forEach(treeNode -> treeBlock.replace(treeNode, treeNode)));

        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(EQUALS.class)))
                .filter(parameters(constraint(hashOfUnknown())))
                // TODO: replace the matched node with a PUSH 0
                .forEach(treeNode -> treeBlock.replace(treeNode, treeNode)));

        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(selfContained())
                // TODO: replace with NOOP PUSH equivalent
                .forEach(treeNode -> treeBlock.replace(treeNode, treeNode)));

        tree.getTreeBlocks().stream()
                .filter(maximumEntries(0))
                .forEach(tree::remove);

        tree.getTreeBlocks().stream()
                .filter(invalidExit(tree))
                // TODO: replace with an instruction throwing a ProgramException
                .forEach(entry -> tree.replace(entry, entry));

        tree.getTreeBlocks().stream()
                .filter(maximumEntries(1))
                .forEach(tree::mergeUpwards);

        tree.getTreeBlocks().stream()
                .filter(maximumExits(1))
                .forEach(tree::mergeDownwards);

        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(NOOP.class)))
                .forEach(treeBlock::remove));

        tree.getTreeBlocks().stream()
                .filter(maximumExits(0))
                // TODO: trim dead code in replacement
                .forEach(treeBlock -> tree.replace(treeBlock, treeBlock));
    }
}
