package com.hileco.cortex.targeting;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphBlock;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.calls.CALL;

import java.util.HashSet;
import java.util.Set;

public class TargetFinder {

    public Set<Pair<GraphBlock, GraphNode>> find(Graph graph) {
        var results = new HashSet<Pair<GraphBlock, GraphNode>>();
        graph.getGraphBlocks()
                .forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                        .filter(graphNode -> graphNode.isInstruction(CALL.class))
                        .forEach(graphNode -> {
                            results.add(new Pair<>(graphBlock, graphNode));
                        })
                );
        return results;
    }
}
