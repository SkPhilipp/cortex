package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Tree {
    @Getter
    private List<TreeBlock> treeBlocks;
    private List<AtomicReference<Instruction>> instructions;

    public Tree() {
        treeBlocks = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public void includeAsTreeBlock(int line, List<AtomicReference<Instruction>> instructions) {
        TreeBlock treeBlock = new TreeBlock();
        treeBlock.include(line, instructions);
        treeBlocks.add(treeBlock);
    }

    public void include(List<Instruction> instructions) {
        List<AtomicReference<Instruction>> blockInstructions = new ArrayList<>();
        int currentBlock = 0;
        int line = 0;
        while (line < instructions.size()) {
            AtomicReference<Instruction> instructionReference = new AtomicReference<>(instructions.get(line));
            if (instructionReference.get() instanceof JUMP_DESTINATION) {
                if (blockInstructions.size() > 0) {
                    includeAsTreeBlock(currentBlock, blockInstructions);
                    currentBlock = line;
                }
                blockInstructions.clear();
            }
            blockInstructions.add(instructionReference);
            this.instructions.add(instructionReference);
            line++;
        }
        if (blockInstructions.size() > 0) {
            includeAsTreeBlock(currentBlock, blockInstructions);
        }
    }

    private int indexOf(TreeBlock treeBlock) {
        int index = treeBlocks.indexOf(treeBlock);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public void mergeUpwards(TreeBlock treeBlock) {
        int index = indexOf(treeBlock);
        if (index == 0) {
            return;
        }
        TreeBlock target = treeBlocks.get(index - 1);
        target.append(treeBlock);
        treeBlocks.remove(treeBlock);
    }

    public void mergeDownwards(TreeBlock treeBlock) {
        int index = indexOf(treeBlock);
        if (index + 1 >= treeBlocks.size()) {
            return;
        }
        TreeBlock target = treeBlocks.get(index + 1);
        treeBlock.append(target);
        treeBlocks.remove(target);
    }

    public void remove(TreeBlock treeBlock) {
        treeBlocks.remove(treeBlock);
    }

    public void replace(TreeBlock original, TreeBlock replacement) {
        int index = indexOf(original);
        treeBlocks.set(index, replacement);
    }

    public List<Instruction> toInstructions() {
        return instructions.stream()
                .map(AtomicReference::get)
                .collect(Collectors.toList());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        for (TreeBlock treeBlock : treeBlocks) {
            stringBuilder.append(treeBlock);
            stringBuilder.append("        │\n");
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
