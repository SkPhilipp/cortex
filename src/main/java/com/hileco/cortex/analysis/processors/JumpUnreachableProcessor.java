package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.instructions.debug.NOOP;

import java.util.HashSet;

public class JumpUnreachableProcessor implements Processor {

    private static final int PROGRAM_START = 0;

    @Override
    public void process(Graph graph) {
        EdgeFlowMapping.UTIL.findAny(graph).ifPresent(edgeFlowMapping -> {
            var targets = new HashSet<Integer>();
            targets.add(PROGRAM_START);
            targets.addAll(edgeFlowMapping.getFlowsToTarget().keySet());
            graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream().findFirst().ifPresent(startingNode -> {
                if (!targets.contains(startingNode.getLine())) {
                    graphBlock.getGraphNodes().forEach(graphNode -> graphNode.getInstruction().set(new NOOP()));
                }
            }));
        });
    }
}
