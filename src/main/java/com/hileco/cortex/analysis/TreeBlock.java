package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class TreeBlock {
    private Set<Integer> knownEntries;
    private Set<Integer> knownExits;
    private Set<Integer> potentialEntries;
    private Set<Integer> potentialExits;
    private List<TreeNode> treeNodes;
    private List<AtomicReference<Instruction>> instructions;

    public TreeBlock() {
        this.knownEntries = new HashSet<>();
        this.knownExits = new HashSet<>();
        this.potentialEntries = new HashSet<>();
        this.potentialExits = new HashSet<>();
        this.treeNodes = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public void include(int lineOffset, List<AtomicReference<Instruction>> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            AtomicReference<Instruction> instructionReference = instructions.get(i);
            TreeNode treeNode = new TreeNode();
            treeNode.setLine(lineOffset + i);
            treeNode.setType(TreeNodeType.INSTRUCTION);
            treeNode.setInstruction(instructionReference);
            this.treeNodes.add(treeNode);
        }
        this.instructions.addAll(instructions);
    }

    void append(TreeBlock other) {
        this.knownEntries.addAll(other.knownEntries);
        this.knownExits.addAll(other.knownExits);
        this.potentialEntries.addAll(other.potentialEntries);
        this.potentialExits.addAll(other.potentialExits);
        this.treeNodes.addAll(other.treeNodes);
        this.instructions.addAll(other.instructions);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TreeNode treeNode : this.treeNodes) {
            stringBuilder.append(treeNode);
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
