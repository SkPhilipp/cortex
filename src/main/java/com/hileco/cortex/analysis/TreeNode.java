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
    private List<TreeNode> parameters;

    public TreeNode() {
        parameters = new ArrayList<>();
    }

    public String toString() {
        return type.format(this);
    }

    private void addInstructionsByLine(List<Pair<Integer, AtomicReference<Instruction>>> list) {
        if (type != INSTRUCTION) {
            throw new IllegalStateException(String.format("Cannot convert type %s to instruction", type));
        }
        list.add(new Pair<>(line, instruction));
        for (TreeNode parameter : parameters) {
            parameter.addInstructionsByLine(list);
        }
    }

    public List<Instruction> toInstructions() {
        List<Pair<Integer, AtomicReference<Instruction>>> list = new ArrayList<>();
        addInstructionsByLine(list);
        list.sort(Comparator.comparingInt(Pair::getKey));
        return list.stream()
                .map(pair -> pair.getValue().get())
                .collect(Collectors.toList());
    }

    public boolean hasParameters(Predicate<TreeNode> predicate) {
        return parameters.stream().allMatch(predicate);
    }

    public boolean hasParameter(int index, Predicate<TreeNode> predicate) {
        return index < parameters.size() && predicate.test(parameters.get(index));
    }

    public boolean isInstruction(Class<?>... classes) {
        return type == INSTRUCTION && (classes.length == 0 || Arrays.stream(classes).anyMatch(aClass -> aClass.isInstance(instruction.get())));
    }

    private boolean fully(Predicate<TreeNode> predicate) {
        return predicate.test(this)
                && hasParameters(treeNode -> treeNode.fully(predicate));
    }

    public boolean isSelfContained() {
        return fully(treeNode -> treeNode.isInstruction()
                && SELF_CONTAINED_ZONES.containsAll(treeNode.getInstruction().get().getInstructionModifiers())
                && !(treeNode.getInstruction().get() instanceof SWAP));
    }
}
