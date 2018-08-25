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
        this.treeBlocks = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    private void includeAsTreeBlock(int line, List<AtomicReference<Instruction>> instructions) {
        TreeBlock treeBlock = new TreeBlock();
        treeBlock.include(line, instructions);
        this.treeBlocks.add(treeBlock);
    }

    public void include(List<Instruction> instructions) {
        List<AtomicReference<Instruction>> blockInstructions = new ArrayList<>();
        int currentBlock = 0;
        int line = 0;
        while (line < instructions.size()) {
            AtomicReference<Instruction> instructionReference = new AtomicReference<>(instructions.get(line));
            if (instructionReference.get() instanceof JUMP_DESTINATION) {
                if (blockInstructions.size() > 0) {
                    this.includeAsTreeBlock(currentBlock, blockInstructions);
                    currentBlock = line;
                }
                blockInstructions.clear();
            }
            blockInstructions.add(instructionReference);
            this.instructions.add(instructionReference);
            line++;
        }
        if (blockInstructions.size() > 0) {
            this.includeAsTreeBlock(currentBlock, blockInstructions);
        }
    }

    private int indexOf(TreeBlock treeBlock) {
        int index = this.treeBlocks.indexOf(treeBlock);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public void mergeUpwards(TreeBlock treeBlock) {
        int index = this.indexOf(treeBlock);
        if (index == 0) {
            return;
        }
        TreeBlock target = this.treeBlocks.get(index - 1);
        target.append(treeBlock);
        this.treeBlocks.remove(treeBlock);
    }

    public void mergeDownwards(TreeBlock treeBlock) {
        int index = this.indexOf(treeBlock);
        if (index + 1 >= this.treeBlocks.size()) {
            return;
        }
        TreeBlock target = this.treeBlocks.get(index + 1);
        treeBlock.append(target);
        this.treeBlocks.remove(target);
    }

    public void remove(TreeBlock treeBlock) {
        this.treeBlocks.remove(treeBlock);
    }

    public void replace(TreeBlock original, TreeBlock replacement) {
        int index = this.indexOf(original);
        this.treeBlocks.set(index, replacement);
    }

    public List<Instruction> toInstructions() {
        return this.instructions.stream()
                .map(AtomicReference::get)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        for (TreeBlock treeBlock : this.treeBlocks) {
            stringBuilder.append(treeBlock);
            stringBuilder.append("        │\n");
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
