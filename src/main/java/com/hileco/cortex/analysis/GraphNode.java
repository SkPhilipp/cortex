package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.edges.Edge;
import com.hileco.cortex.analysis.edges.EdgeParameters;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.stack.SWAP;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class GraphNode {
    private static final Set<ProgramZone> SELF_CONTAINED_ZONES = new HashSet<>(Collections.singleton(ProgramZone.STACK));
    private AtomicReference<Instruction> instruction;
    private Integer line;
    private final ArrayList<Edge> edges;

    public GraphNode() {
        this.edges = new ArrayList<>();
    }

    private void addInstructionsByLine(List<Pair<Integer, AtomicReference<Instruction>>> list) {
        list.add(new Pair<>(this.line, this.instruction));
        this.edges.stream()
                .filter(edge -> edge.getClass() == EdgeParameters.class)
                .map(edge -> (EdgeParameters) edge)
                .map(EdgeParameters::getGraphNodes)
                .flatMap(Collection::stream)
                .forEach(node -> node.addInstructionsByLine(list));
    }

    public List<Instruction> toInstructions() {
        List<Pair<Integer, AtomicReference<Instruction>>> list = new ArrayList<>();
        this.addInstructionsByLine(list);
        list.sort(Comparator.comparingInt(Pair::getKey));
        return list.stream()
                .map(pair -> pair.getValue().get())
                .collect(Collectors.toList());
    }

    private boolean hasParameters(Predicate<GraphNode> predicate) {
        return this.edges.stream()
                .filter(edge -> edge.getClass() == EdgeParameters.class)
                .map(edge -> (EdgeParameters) edge)
                .flatMap(edgeParameters -> edgeParameters.getGraphNodes().stream())
                .allMatch(predicate);
    }

    public List<GraphNode> getParameters() {
        return this.edges.stream()
                .filter(edge -> edge.getClass() == EdgeParameters.class)
                .map(edge -> (EdgeParameters) edge)
                .map(EdgeParameters::getGraphNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public boolean hasOneParameter(int index, Predicate<GraphNode> predicate) {
        var nodes = this.edges.stream()
                .filter(edge -> edge.getClass() == EdgeParameters.class)
                .map(edge -> (EdgeParameters) edge)
                .map(edge -> edge.getGraphNodes().get(index))
                .collect(Collectors.toList());
        return nodes.size() == 1 && nodes.stream().allMatch(predicate);
    }

    private boolean isInstruction(Stream<Class<?>> classes) {
        return classes.anyMatch(aClass -> aClass.isInstance(this.instruction.get()));
    }

    public boolean isInstruction(Collection<Class<?>> classes) {
        return this.isInstruction(classes.stream());
    }

    public boolean isInstruction(Class<?>... classes) {
        return this.isInstruction(Arrays.stream(classes));
    }

    private boolean fully(Predicate<GraphNode> predicate) {
        return predicate.test(this)
                && this.hasParameters(graphNode -> graphNode != null && graphNode.fully(predicate));
    }

    public boolean isSelfContained() {
        // TODO: And ensure that all child parameter do not have multiple parameter-consumers
        return this.fully(graphNode -> SELF_CONTAINED_ZONES.containsAll(graphNode.getInstruction().get().getInstructionModifiers())
                && !(graphNode.getInstruction().get() instanceof SWAP));
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        this.format(this, stringBuilder, 0);
        return stringBuilder.toString();
    }

    public void format(GraphNode graphNode, StringBuilder stringBuilder, int offset) {
        if (graphNode != null) {
            stringBuilder.append(String.format(" %06d │", graphNode.getLine()));
        } else {
            stringBuilder.append("        │");
        }
        for (var i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        if (graphNode != null) {
            stringBuilder.append(graphNode.getInstruction().toString().trim());
            if (!graphNode.getParameters().isEmpty()) {
                stringBuilder.append(' ');
                for (var parameter : graphNode.getParameters()) {
                    stringBuilder.append('\n');
                    this.format(parameter, stringBuilder, offset + 2);
                }
            }
        } else {
            stringBuilder.append("?");
        }
    }
}
