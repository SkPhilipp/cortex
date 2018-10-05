package com.hileco.cortex.analysis;

import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.stack.SWAP;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hileco.cortex.analysis.GraphNodeType.INSTRUCTION;

@Getter
public class GraphNode {
    private static final Set<ProgramZone> SELF_CONTAINED_ZONES = new HashSet<>(Collections.singleton(ProgramZone.STACK));
    private final List<GraphNode> parameters;
    @Setter
    private GraphNodeType type;
    @Setter
    private AtomicReference<Instruction> instruction;
    @Setter
    private Integer line;

    public GraphNode() {
        this.parameters = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.type.format(this);
    }

    private void addInstructionsByLine(List<Pair<Integer, AtomicReference<Instruction>>> list) {
        if (this.type != INSTRUCTION) {
            throw new IllegalStateException(String.format("Cannot convert type %s to instruction", this.type));
        }
        list.add(new Pair<>(this.line, this.instruction));
        for (var parameter : this.parameters) {
            parameter.addInstructionsByLine(list);
        }
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
        return this.parameters.stream().allMatch(predicate);
    }

    public boolean hasParameter(int index, Predicate<GraphNode> predicate) {
        return index < this.parameters.size() && predicate.test(this.parameters.get(index));
    }

    public boolean isInstruction(Class<?>... classes) {
        return this.type == INSTRUCTION && (classes.length == 0 || Arrays.stream(classes).anyMatch(aClass -> aClass.isInstance(this.instruction.get())));
    }

    private boolean fully(Predicate<GraphNode> predicate) {
        return predicate.test(this)
                && this.hasParameters(graphNode -> graphNode.fully(predicate));
    }

    public boolean isSelfContained() {
        return this.fully(graphNode -> graphNode.isInstruction()
                && SELF_CONTAINED_ZONES.containsAll(graphNode.getInstruction().get().getInstructionModifiers())
                && !(graphNode.getInstruction().get() instanceof SWAP));
    }
}
