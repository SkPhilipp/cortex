package com.hileco.cortex.tree.optimizing;

import com.hileco.cortex.tree.TreeBranch;

import java.util.ArrayList;
import java.util.List;

public class TreeOptimizer {

    private List<TreeOptimizerStrategy> strategies;

    public TreeOptimizer() {
        this.strategies = new ArrayList<>();
    }

    public void optimize(TreeBranch treeBranch) {
        strategies.forEach(treeOptimizerStrategy -> treeOptimizerStrategy.optimize(treeBranch));
    }
}
