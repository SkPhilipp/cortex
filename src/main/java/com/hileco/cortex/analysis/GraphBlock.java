package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.edges.Edge;
import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class GraphBlock {
    private final List<GraphNode> graphNodes;
    private final List<Edge> edges;

    public GraphBlock() {
        this.graphNodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public void include(int lineOffset, List<AtomicReference<Instruction>> instructions) {
        for (var i = 0; i < instructions.size(); i++) {
            var instructionReference = instructions.get(i);
            var graphNode = new GraphNode();
            graphNode.setLine(lineOffset + i);
            graphNode.setInstruction(instructionReference);
            this.graphNodes.add(graphNode);
        }
    }

    void append(GraphBlock other) {
        this.graphNodes.addAll(other.graphNodes);
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
