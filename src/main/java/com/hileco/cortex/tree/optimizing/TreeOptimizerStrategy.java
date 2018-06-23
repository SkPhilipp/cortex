package com.hileco.cortex.tree.optimizing;

import com.hileco.cortex.tree.TreeBranch;

public interface TreeOptimizerStrategy {
    void optimize(TreeBranch treeBranch);
}
