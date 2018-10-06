package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphBlock;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TreeMapper {

    private static final Set<Class<?>> JUMPS = Set.of(JUMP.class, JUMP_IF.class);

    private void mapLinesToBlocksForNode(TreeMapping treeMapping, GraphBlock graphBlock, GraphNode graphNode) {
        var line = graphNode.getLine();
        if (line != null) {
            treeMapping.putLineMapping(line, graphBlock);
        }
        graphNode.getParameters().stream()
                .filter(Objects::nonNull)
                .forEach(parameter -> this.mapLinesToBlocksForNode(treeMapping, graphBlock, parameter));
    }

    private void mapLinesToBlocks(TreeMapping treeMapping, List<GraphBlock> graphBlocks) {
        graphBlocks.forEach(graphBlock -> graphBlock.getGraphNodes().forEach(graphNode -> this.mapLinesToBlocksForNode(treeMapping, graphBlock, graphNode)));
    }

    private void mapJumpsToBlocks(TreeMapping treeMapping, List<GraphBlock> graphBlocks) {
        graphBlocks.forEach(treeBlock -> treeBlock.getGraphNodes()
                .stream()
                .filter(graphNode -> JUMPS.contains(graphNode.getInstruction().get().getClass()))
                .filter(graphNode -> graphNode.hasOneParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(graphNode -> {
                    var targetPushInstruction = (PUSH) graphNode.getParameters().get(0).getInstruction().get();
                    var target = new BigInteger(targetPushInstruction.getBytes()).intValue();
                    treeMapping.putJumpMapping(graphNode.getLine(), target);
                }));
    }

    private void mapBlocksToBlocks(TreeMapping treeMapping, List<GraphBlock> graphBlocks) {
        var graphBlocksLimit = graphBlocks.size();
        if (graphBlocksLimit >= 2) {
            for (var i = 0; i < graphBlocksLimit - 1; i++) {
                var a = graphBlocks.get(i).getGraphNodes();
                var b = graphBlocks.get(i + 1).getGraphNodes();
                if (!a.isEmpty()
                        && !b.isEmpty()
                        && a.stream().noneMatch(graphNode -> graphNode.isInstruction(EXIT.class, JUMP.class))) {
                    treeMapping.putJumpMapping(a.get(0).getLine(), b.get(0).getLine());
                }
            }
        }
    }

    private void mapBlocksToJumps(TreeMapping treeMapping, List<GraphBlock> graphBlocks) {
        graphBlocks.forEach(graphBlock -> {
            var graphNodes = graphBlock.getGraphNodes();
            if (!graphNodes.isEmpty()) {
                var graphBlockStart = graphNodes.get(0).getLine();
                graphNodes.stream()
                        .filter(graphNode -> JUMPS.contains(graphNode.getInstruction().get().getClass()))
                        .forEach(graphNode -> treeMapping.putJumpMapping(graphBlockStart, graphNode.getLine()));
            }
        });
    }

    public TreeMapping map(Graph graph) {
        var treeMapping = new TreeMapping();
        var graphBlocks = graph.getGraphBlocks();
        this.mapLinesToBlocks(treeMapping, graphBlocks);
        this.mapJumpsToBlocks(treeMapping, graphBlocks);
        this.mapBlocksToJumps(treeMapping, graphBlocks);
        this.mapBlocksToBlocks(treeMapping, graphBlocks);
        return treeMapping;
    }
}
