package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Graph {
    @Getter
    private final List<GraphBlock> graphBlocks;
    private final List<AtomicReference<Instruction>> instructions;

    public Graph() {
        this.graphBlocks = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    private void includeAsBlock(int line, List<AtomicReference<Instruction>> instructions) {
        var block = new GraphBlock();
        block.include(line, instructions);
        this.graphBlocks.add(block);
    }

    public void include(List<Instruction> instructions) {
        List<AtomicReference<Instruction>> blockInstructions = new ArrayList<>();
        var currentBlock = 0;
        var line = 0;
        while (line < instructions.size()) {
            AtomicReference<Instruction> instructionReference = new AtomicReference<>(instructions.get(line));
            if (instructionReference.get() instanceof JUMP_DESTINATION) {
                if (!blockInstructions.isEmpty()) {
                    this.includeAsBlock(currentBlock, blockInstructions);
                    currentBlock = line;
                }
                blockInstructions.clear();
            }
            blockInstructions.add(instructionReference);
            this.instructions.add(instructionReference);
            line++;
        }
        if (!blockInstructions.isEmpty()) {
            this.includeAsBlock(currentBlock, blockInstructions);
        }
    }

    private int indexOf(GraphBlock graphBlock) {
        var index = this.graphBlocks.indexOf(graphBlock);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public void mergeUpwards(GraphBlock graphBlock) {
        var index = this.indexOf(graphBlock);
        if (index == 0) {
            return;
        }
        var target = this.graphBlocks.get(index - 1);
        target.append(graphBlock);
        this.graphBlocks.remove(graphBlock);
    }

    public void mergeDownwards(GraphBlock graphBlock) {
        var index = this.indexOf(graphBlock);
        if (index + 1 >= this.graphBlocks.size()) {
            return;
        }
        var target = this.graphBlocks.get(index + 1);
        graphBlock.append(target);
        this.graphBlocks.remove(target);
    }

    public void remove(GraphBlock graphBlock) {
        this.graphBlocks.remove(graphBlock);
    }

    public void replace(GraphBlock original, GraphBlock replacement) {
        var index = this.indexOf(original);
        this.graphBlocks.set(index, replacement);
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
        for (var graphBlock : this.graphBlocks) {
            stringBuilder.append(graphBlock);
            stringBuilder.append("        │\n");
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
