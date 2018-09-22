package com.hileco.cortex.targeting;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBlock;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.calls.CALL;

import java.util.HashSet;
import java.util.Set;

public class TargetFinder {

    public Set<Pair<TreeBlock, TreeNode>> find(Tree tree) {
        var results = new HashSet<Pair<TreeBlock, TreeNode>>();
        tree.getTreeBlocks()
                .forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                        .filter(treeNode -> treeNode.isInstruction(CALL.class))
                        .forEach(treeNode -> {
                            results.add(new Pair<>(treeBlock, treeNode));
                        })
                );
        return results;
    }

}
