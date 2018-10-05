package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class GraphBlock {
    private final Set<Integer> knownEntries;
    private final Set<Integer> knownExits;
    private final Set<Integer> potentialEntries;
    private final Set<Integer> potentialExits;
    private final List<GraphNode> graphNodes;
    private final List<AtomicReference<Instruction>> instructions;

    public GraphBlock() {
        this.knownEntries = new HashSet<>();
        this.knownExits = new HashSet<>();
        this.potentialEntries = new HashSet<>();
        this.potentialExits = new HashSet<>();
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
        this.knownEntries.addAll(other.knownEntries);
        this.knownExits.addAll(other.knownExits);
        this.potentialEntries.addAll(other.potentialEntries);
        this.potentialExits.addAll(other.potentialExits);
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

    public int countEntries() {
        return this.knownEntries.size() + this.potentialEntries.size();
    }

    public int countExits() {
        return this.knownExits.size() + this.potentialExits.size();
    }
}
