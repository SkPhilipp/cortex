package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.edges.Edge;
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
    @Getter
    private final List<Edge> edges;

    public Graph() {
        this.graphBlocks = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Graph(List<Instruction> instructions) {
        this();
        var block = new ArrayList<AtomicReference<Instruction>>();
        var currentBlockLine = 0;
        var currentLine = 0;
        while (currentLine < instructions.size()) {
            AtomicReference<Instruction> instructionReference = new AtomicReference<>(instructions.get(currentLine));
            if (instructionReference.get() instanceof JUMP_DESTINATION) {
                if (!block.isEmpty()) {
                    this.includeAsBlock(currentBlockLine, block);
                    currentBlockLine = currentLine;
                }
                block.clear();
            }
            block.add(instructionReference);
            currentLine++;
        }
        if (!block.isEmpty()) {
            this.includeAsBlock(currentBlockLine, block);
        }
    }

    private void includeAsBlock(int line, List<AtomicReference<Instruction>> instructions) {
        var block = new GraphBlock();
        block.include(line, instructions);
        this.graphBlocks.add(block);
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
        return this.graphBlocks.stream()
                .flatMap(graphBlock -> graphBlock.getGraphNodes().stream())
                .map(graphNode -> graphNode.getInstruction().get())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        for (var graphBlock : this.graphBlocks) {
            stringBuilder.append(graphBlock);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
