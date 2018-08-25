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
    private final List<TreeBlock> treeBlocks;
    private final List<AtomicReference<Instruction>> instructions;

    public Tree() {
        this.treeBlocks = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    private void includeAsTreeBlock(int line, List<AtomicReference<Instruction>> instructions) {
        var treeBlock = new TreeBlock();
        treeBlock.include(line, instructions);
        this.treeBlocks.add(treeBlock);
    }

    public void include(List<Instruction> instructions) {
        List<AtomicReference<Instruction>> blockInstructions = new ArrayList<>();
        var currentBlock = 0;
        var line = 0;
        while (line < instructions.size()) {
            AtomicReference<Instruction> instructionReference = new AtomicReference<>(instructions.get(line));
            if (instructionReference.get() instanceof JUMP_DESTINATION) {
                if (!blockInstructions.isEmpty()) {
                    this.includeAsTreeBlock(currentBlock, blockInstructions);
                    currentBlock = line;
                }
                blockInstructions.clear();
            }
            blockInstructions.add(instructionReference);
            this.instructions.add(instructionReference);
            line++;
        }
        if (!blockInstructions.isEmpty()) {
            this.includeAsTreeBlock(currentBlock, blockInstructions);
        }
    }

    private int indexOf(TreeBlock treeBlock) {
        var index = this.treeBlocks.indexOf(treeBlock);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public void mergeUpwards(TreeBlock treeBlock) {
        var index = this.indexOf(treeBlock);
        if (index == 0) {
            return;
        }
        var target = this.treeBlocks.get(index - 1);
        target.append(treeBlock);
        this.treeBlocks.remove(treeBlock);
    }

    public void mergeDownwards(TreeBlock treeBlock) {
        var index = this.indexOf(treeBlock);
        if (index + 1 >= this.treeBlocks.size()) {
            return;
        }
        var target = this.treeBlocks.get(index + 1);
        treeBlock.append(target);
        this.treeBlocks.remove(target);
    }

    public void remove(TreeBlock treeBlock) {
        this.treeBlocks.remove(treeBlock);
    }

    public void replace(TreeBlock original, TreeBlock replacement) {
        var index = this.indexOf(original);
        this.treeBlocks.set(index, replacement);
    }

    public List<Instruction> toInstructions() {
        return this.instructions.stream()
                .map(AtomicReference::get)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        for (var treeBlock : this.treeBlocks) {
            stringBuilder.append(treeBlock);
            stringBuilder.append("        │\n");
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
