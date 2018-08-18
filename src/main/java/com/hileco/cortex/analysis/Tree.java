package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Tree {
    private List<TreeBlock> treeBlocks;
    private List<Instruction> instructions;

    public Tree() {
        treeBlocks = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public void includeAsTreeBlock(int line, List<Instruction> instructions) {
        TreeBlock treeBlock = new TreeBlock();
        treeBlock.include(line, instructions);
        treeBlocks.add(treeBlock);
    }

    public void include(List<Instruction> instructions) {
        List<Instruction> blockInstructions = new ArrayList<>();
        int currentBlock = 0;
        int line = 0;
        while (line < instructions.size()) {
            Instruction instruction = instructions.get(line);
            if (instruction instanceof JUMP_DESTINATION) {
                if (blockInstructions.size() > 0) {
                    includeAsTreeBlock(currentBlock, blockInstructions);
                    currentBlock = line;
                }
                blockInstructions.clear();
            }
            blockInstructions.add(instruction);
            line++;
        }
        if (blockInstructions.size() > 0) {
            includeAsTreeBlock(currentBlock, blockInstructions);
        }
        this.instructions.addAll(instructions);
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Tree Start:\n");
        for (TreeBlock treeBlock : treeBlocks) {
            stringBuilder.append(treeBlock);
        }
        stringBuilder.append("\nTree End.");
        return stringBuilder.toString();
    }
}
