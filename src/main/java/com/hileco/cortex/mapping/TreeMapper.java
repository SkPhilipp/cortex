package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBlock;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.analysis.TreeNodeType;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class TreeMapper {

    private static final Set<Class<?>> JUMPS = Set.of(JUMP.class, JUMP_IF.class);

    private void mapLinesToBlocksForNode(TreeMapping treeMapping, TreeBlock treeBlock, TreeNode treeNode) {
        var line = treeNode.getLine();
        if (line != null) {
            treeMapping.putLineMapping(line, treeBlock);
        }
        var parameters = treeNode.getParameters();
        if (parameters != null) {
            parameters.forEach(parameter -> this.mapLinesToBlocksForNode(treeMapping, treeBlock, parameter));
        }
    }

    private void mapLinesToBlocks(TreeMapping treeMapping, List<TreeBlock> treeBlocks) {
        treeBlocks.forEach(treeBlock -> treeBlock.getTreeNodes().forEach(treeNode -> this.mapLinesToBlocksForNode(treeMapping, treeBlock, treeNode)));
    }

    private void mapJumpsToBlocks(TreeMapping treeMapping, List<TreeBlock> treeBlocks) {
        treeBlocks.forEach(treeBlock -> treeBlock.getTreeNodes()
                .stream()
                .filter(treeNode -> treeNode.getType() == TreeNodeType.INSTRUCTION)
                .filter(treeNode -> JUMPS.contains(treeNode.getInstruction().get().getClass()))
                .filter(treeNode -> treeNode.hasParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(treeNode -> {
                    var targetPushInstruction = (PUSH) treeNode.getParameters().get(0).getInstruction().get();
                    var target = new BigInteger(targetPushInstruction.getBytes()).intValue();
                    treeMapping.putJumpMapping(treeNode.getLine(), target);
                }));
    }

    private void mapBlocksToBlocks(TreeMapping treeMapping, List<TreeBlock> treeBlocks) {
        var treeBlocksLimit = treeBlocks.size();
        if (treeBlocksLimit >= 2) {
            for (var i = 0; i < treeBlocksLimit - 1; i++) {
                var a = treeBlocks.get(i).getTreeNodes();
                var b = treeBlocks.get(i + 1).getTreeNodes();
                if (!a.isEmpty()
                        && !b.isEmpty()
                        && a.stream().noneMatch(treeNode -> treeNode.isInstruction(EXIT.class, JUMP.class))) {
                    treeMapping.putJumpMapping(a.get(0).getLine(), b.get(0).getLine());
                }
            }
        }
    }

    private void mapBlocksToJumps(TreeMapping treeMapping, List<TreeBlock> treeBlocks) {
        treeBlocks.forEach(treeBlock -> {
            var treeNodes = treeBlock.getTreeNodes();
            if (!treeNodes.isEmpty()) {
                var treeBlockStart = treeNodes.get(0).getLine();
                treeNodes.stream()
                        .filter(treeNode -> JUMPS.contains(treeNode.getInstruction().get().getClass()))
                        .forEach(treeNode -> treeMapping.putJumpMapping(treeBlockStart, treeNode.getLine()));
            }
        });
    }

    public TreeMapping map(Tree tree) {
        var treeMapping = new TreeMapping();
        var treeBlocks = tree.getTreeBlocks();
        this.mapLinesToBlocks(treeMapping, treeBlocks);
        this.mapJumpsToBlocks(treeMapping, treeBlocks);
        this.mapBlocksToJumps(treeMapping, treeBlocks);
        this.mapBlocksToBlocks(treeMapping, treeBlocks);
        return treeMapping;
    }
}
