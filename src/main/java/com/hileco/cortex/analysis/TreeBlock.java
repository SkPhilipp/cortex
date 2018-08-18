package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class TreeBlock {
    private Set<Integer> knownEntries;
    private Set<Integer> knownExits;
    private Set<Integer> potentialEntries;
    private Set<Integer> potentialExits;
    private List<TreeNode> treeNodes;
    private List<Instruction> instructions;

    public TreeBlock() {
        knownEntries = new HashSet<>();
        knownExits = new HashSet<>();
        potentialEntries = new HashSet<>();
        potentialExits = new HashSet<>();
        treeNodes = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public void include(int lineOffset, List<Instruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            TreeNode treeNode = new TreeNode();
            treeNode.setLine(lineOffset + i);
            treeNode.setType(TreeNodeType.INSTRUCTION);
            treeNode.setInstruction(instruction);
            treeNodes.add(treeNode);
        }
        this.instructions.addAll(instructions);
    }

    public void remove(TreeNode treeNode) {
        // TODO: Implement
    }

    public void replace(TreeNode original, TreeNode replacement) {
        // TODO: Implement
    }

    void append(TreeBlock other) {
        knownEntries.addAll(other.knownEntries);
        knownExits.addAll(other.knownExits);
        potentialEntries.addAll(other.potentialEntries);
        potentialExits.addAll(other.potentialExits);
        treeNodes.addAll(other.treeNodes);
        instructions.addAll(other.instructions);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        for (TreeNode treeNode : treeNodes) {
            stringBuilder.append(treeNode);
            stringBuilder.append('\n');
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
