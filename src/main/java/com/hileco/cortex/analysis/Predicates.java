package com.hileco.cortex.analysis;

import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import static com.hileco.cortex.analysis.TreeNodeType.INSTRUCTION;
import static com.hileco.cortex.context.ProgramZone.STACK;

public class Predicates {
    public static Predicate<TreeBlock> maximumEntries(int i) {
        return treeBlock -> treeBlock.getKnownEntries().size() + treeBlock.getPotentialEntries().size() <= i;
    }

    public static Predicate<TreeBlock> maximumExits(int i) {
        return treeBlock -> treeBlock.getKnownExits().size() + treeBlock.getPotentialExits().size() <= i;
    }

    public static Predicate<TreeBlock> invalidExit(Tree tree) {
        // TODO: Implement
        return (treeBlock) -> true;
    }

    public static Predicate<TreeNode> instruction() {
        return node -> node != null && node.getType() == INSTRUCTION;
    }

    public static Predicate<TreeNode> instruction(Predicate<Instruction> predicate) {
        return instruction().and(treeNode -> predicate.test(treeNode.getInstruction()));
    }

    public static Predicate<TreeNode> parameters(Predicate<TreeNode> predicate) {
        return treeNode -> treeNode.getParameters().stream().allMatch(predicate);
    }

    public static Predicate<TreeNode> parameter(int index, Predicate<TreeNode> predicate) {
        return treeNode -> {
            TreeNode parameter = treeNode.getParameters().get(index);
            return parameter != null && predicate.test(parameter);
        };
    }

    public static Predicate<TreeNode> constraint(Predicate<TreeNode> predicate) {
        // TODO: Implement
        return t -> true;
    }

    public static Predicate<TreeNode> equalsToStatic() {
        // TODO: Implement
        return t -> true;
    }

    public static Predicate<TreeNode> hashOfUnknown() {
        // TODO: Implement
        return t -> true;
    }

    public static Predicate<Instruction> type(Class<?>... classes) {
        return instruction -> Arrays.stream(classes).anyMatch(aClass -> aClass.isInstance(instruction));
    }

    private static final Set<ProgramZone> STACK_ZONE = Collections.singleton(STACK);

    public static Predicate<TreeNode> selfContained() {
        // TODO: Implement
        return t -> true;
    }

    public static Predicate<TreeNode> fully(Predicate<TreeNode> predicate) {
        return treeNode -> predicate.test(treeNode) && parameters(fully(predicate)).test(treeNode);
    }
}
