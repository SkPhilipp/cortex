package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBlock;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.analysis.TreeNodeType;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.Set;

public class TreeMapper {

    private static final Set<Class<?>> JUMPS = Set.of(JUMP.class, JUMP_IF.class);

    private void lineMapNode(TreeMapping treeMapping, TreeBlock treeBlock, TreeNode treeNode) {
        var line = treeNode.getLine();
        if (line != null) {
            treeMapping.putLineMapping(line, treeBlock);
        }
        var parameters = treeNode.getParameters();
        if (parameters != null) {
            parameters.forEach(parameter -> this.lineMapNode(treeMapping, treeBlock, parameter));
        }
    }

    private void lineMapBlock(TreeMapping treeMapping, TreeBlock treeBlock) {
        treeBlock.getTreeNodes().forEach(treeNode -> this.lineMapNode(treeMapping, treeBlock, treeNode));
    }

    public TreeMapping map(Tree tree) {
        var treeMapping = new TreeMapping();
        var treeBlocks = tree.getTreeBlocks();
        treeBlocks.forEach(treeBlock -> this.lineMapBlock(treeMapping, treeBlock));
        treeBlocks.forEach(treeBlock -> treeBlock.getTreeNodes()
                .stream()
                .filter(treeNode -> treeNode.getType() == TreeNodeType.INSTRUCTION)
                .filter(treeNode -> JUMPS.contains(treeNode.getInstruction().get().getClass()))
                .filter(treeNode -> treeNode.hasParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(treeNode -> {
                    var targetPushInstruction = (PUSH) treeNode.getParameters().get(0).getInstruction().get();
                    var target = new BigInteger(targetPushInstruction.getBytes()).intValue();
                    treeMapping.putJumpMapping(treeBlock, treeNode.getLine(), target);
                }));
        // TODO: Map blocks which do not end in a JUMP or EXIT to their next block
        return treeMapping;
    }
}
