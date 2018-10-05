package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class GraphBlock {
    private final List<GraphNode> graphNodes;
    private final List<AtomicReference<Instruction>> instructions;

    public GraphBlock() {
        this.graphNodes = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public void include(int lineOffset, List<AtomicReference<Instruction>> instructions) {
        for (var i = 0; i < instructions.size(); i++) {
            var instructionReference = instructions.get(i);
            var graphNode = new GraphNode();
            graphNode.setLine(lineOffset + i);
            graphNode.setType(GraphNodeType.INSTRUCTION);
            graphNode.setInstruction(instructionReference);
            this.graphNodes.add(graphNode);
        }
        this.instructions.addAll(instructions);
    }

    void append(GraphBlock other) {
        this.graphNodes.addAll(other.graphNodes);
        this.instructions.addAll(other.instructions);
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        for (var graphNode : this.graphNodes) {
            stringBuilder.append(graphNode);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
