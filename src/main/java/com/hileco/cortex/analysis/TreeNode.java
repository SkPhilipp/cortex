package com.hileco.cortex.analysis;

import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.stack.SWAP;
import javafx.util.Pair;
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

import static com.hileco.cortex.analysis.TreeNodeType.INSTRUCTION;

@Getter
public class TreeNode {
    private static final Set<ProgramZone> SELF_CONTAINED_ZONES = new HashSet<>(Collections.singleton(ProgramZone.STACK));

    @Setter
    private TreeNodeType type;
    @Setter
    private AtomicReference<Instruction> instruction;
    @Setter
    private Integer line;
    private final List<TreeNode> parameters;

    public TreeNode() {
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

    private boolean hasParameters(Predicate<TreeNode> predicate) {
        return this.parameters.stream().allMatch(predicate);
    }

    public boolean hasParameter(int index, Predicate<TreeNode> predicate) {
        return index < this.parameters.size() && predicate.test(this.parameters.get(index));
    }

    public boolean isInstruction(Class<?>... classes) {
        return this.type == INSTRUCTION && (classes.length == 0 || Arrays.stream(classes).anyMatch(aClass -> aClass.isInstance(this.instruction.get())));
    }

    private boolean fully(Predicate<TreeNode> predicate) {
        return predicate.test(this)
                && this.hasParameters(treeNode -> treeNode.fully(predicate));
    }

    public boolean isSelfContained() {
        return this.fully(treeNode -> treeNode.isInstruction()
                && SELF_CONTAINED_ZONES.containsAll(treeNode.getInstruction().get().getInstructionModifiers())
                && !(treeNode.getInstruction().get() instanceof SWAP));
    }
}
