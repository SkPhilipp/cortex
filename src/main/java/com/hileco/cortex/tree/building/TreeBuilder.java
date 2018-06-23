package com.hileco.cortex.tree.building;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.tree.TreeBranch;

import java.util.ArrayList;
import java.util.List;

public class TreeBuilder {

    private List<TreeBuilderStrategy> strategies;

    public TreeBuilder() {
        this.strategies = new ArrayList<>();
    }

    public TreeBranch asTree(List<Instruction> instructions) {
        TreeBranch root = new TreeBranch();
        for (Instruction instruction : instructions) {
            TreeBranch branch = new TreeBranch();
            branch.setInstruction(instruction);
            root.getBranches().add(branch);
        }
        strategies.forEach(treeBuilderStrategy -> treeBuilderStrategy.split(root));
        return root;
    }
}
