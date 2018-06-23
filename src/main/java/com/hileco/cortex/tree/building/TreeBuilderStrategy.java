package com.hileco.cortex.tree.building;

import com.hileco.cortex.tree.TreeBranch;

public interface TreeBuilderStrategy {
    void split(TreeBranch treeBranch);
}
