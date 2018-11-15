package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphBlock;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.analysis.edges.EdgeFlow;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.edges.EdgeFlowType;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.calls.CALL_RETURN;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.hileco.cortex.analysis.edges.EdgeFlowType.BLOCK_PART;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_CALL;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_CALL_RETURN;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_EXIT;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_HALT;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP_IF;

public class FlowProcessor implements Processor {

    private static final Set<Class<?>> FLOW_CLASSES_JUMPS = Set.of(JUMP.class, JUMP_IF.class);
    private static final Set<Class<?>> FLOW_CLASSES_OTHERS = Set.of(HALT.class, EXIT.class, CALL_RETURN.class, CALL.class);
    private static final Class<?>[] GUARANTEED_ENDS = {JUMP.class, HALT.class, EXIT.class, CALL_RETURN.class};
    private static final Map<Class<?>, EdgeFlowType> FLOW_TYPE_MAPPING = new HashMap<>();

    static {
        FLOW_TYPE_MAPPING.put(CALL.class, INSTRUCTION_CALL);
        FLOW_TYPE_MAPPING.put(CALL_RETURN.class, INSTRUCTION_CALL_RETURN);
        FLOW_TYPE_MAPPING.put(JUMP_IF.class, INSTRUCTION_JUMP_IF);
        FLOW_TYPE_MAPPING.put(JUMP.class, INSTRUCTION_JUMP);
        FLOW_TYPE_MAPPING.put(EXIT.class, INSTRUCTION_EXIT);
        FLOW_TYPE_MAPPING.put(HALT.class, INSTRUCTION_HALT);
    }

    private void mapLinesToBlocksForNode(EdgeFlowMapping edge, GraphBlock graphBlock, GraphNode graphNode) {
        var line = graphNode.getLine();
        if (line != null) {
            edge.putLineMapping(line, graphBlock);
        }
        graphNode.getParameters().stream()
                .filter(Objects::nonNull)
                .forEach(parameter -> this.mapLinesToBlocksForNode(edge, graphBlock, parameter));
    }

    @Override
    public void process(Graph graph) {
        var graphEdge = new EdgeFlowMapping();
        var graphBlocks = graph.getGraphBlocks();

        // map lines to blocks
        graphBlocks.forEach(graphBlock -> graphBlock.getGraphNodes()
                .forEach(graphNode -> this.mapLinesToBlocksForNode(graphEdge, graphBlock, graphNode)));

        // other flow instructions
        graphBlocks.forEach(graphBlock -> graphBlock.getGraphNodes().forEach(graphNode -> {
            if (graphNode.isInstruction(FLOW_CLASSES_OTHERS)) {
                var instructionClass = graphNode.getInstruction().get().getClass();
                var edgeFlowType = FLOW_TYPE_MAPPING.get(instructionClass);
                var edgeFlow = new EdgeFlow(edgeFlowType, graphNode.getLine(), null);
                graphNode.getEdges().add(edgeFlow);
                graphEdge.map(edgeFlow);
            }
        }));

        // map jumps to blocks
        graphBlocks.forEach(graphBlock -> graphBlock.getGraphNodes()
                .stream()
                .filter(graphNode -> graphNode.isInstruction(FLOW_CLASSES_JUMPS))
                .filter(graphNode -> graphNode.hasOneParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(graphNode -> {
                    var targetPushInstruction = (PUSH) graphNode.getParameters().get(0).getInstruction().get();
                    var target = new BigInteger(targetPushInstruction.getBytes()).intValue();
                    var edgeFlowType = FLOW_TYPE_MAPPING.get(graphNode.getInstruction().get().getClass());
                    var edgeFlow = new EdgeFlow(edgeFlowType, graphNode.getLine(), target);
                    graphNode.getEdges().add(edgeFlow);
                    graphEdge.map(edgeFlow);
                }));

        // map blocks to jumps
        graphBlocks.forEach(graphBlock -> {
            var graphNodes = graphBlock.getGraphNodes();
            if (!graphNodes.isEmpty()) {
                var graphBlockStart = graphNodes.get(0).getLine();
                graphNodes.stream()
                        .filter(graphNode -> graphNode.isInstruction(FLOW_CLASSES_JUMPS))
                        .forEach(graphNode -> {
                            var edgeFlow = new EdgeFlow(BLOCK_PART, graphBlockStart, graphNode.getLine());
                            graphEdge.map(edgeFlow);
                        });
            }
        });

        // map blocks to blocks
        var graphBlocksLimit = graphBlocks.size();
        if (graphBlocksLimit >= 2) {
            for (var i = 0; i < graphBlocksLimit - 1; i++) {
                var graphBlockA = graphBlocks.get(i);
                var graphBlockB = graphBlocks.get(i + 1);
                var graphNodesA = graphBlockA.getGraphNodes();
                var graphNodesB = graphBlockB.getGraphNodes();
                if (!graphNodesA.isEmpty()
                        && !graphNodesB.isEmpty()
                        && graphNodesA.stream().noneMatch(graphNode -> graphNode.isInstruction(GUARANTEED_ENDS))) {
                    // TODO: Don't map blocks to block here; instead if a block contains no guaranteed ends
                    // TODO:   it should inherit all the mappings of the next block, continuing until either
                    // TODO:   the last block is reached or a block is found which does contain a guaranteed end
                    var graphNodeA = graphNodesA.get(0);
                    var graphNodeB = graphNodesB.get(0);
                    var edgeFlow = new EdgeFlow(EdgeFlowType.BLOCK_END, graphNodeA.getLine(), graphNodeB.getLine());
                    graphEdge.map(edgeFlow);
                    graphBlockA.getEdges().add(edgeFlow);
                }
            }
        }

        // map program start
        if (graphBlocksLimit > 0) {
            var graphBlockStart = graphBlocks.get(0);
            var edgeFlow = new EdgeFlow(EdgeFlowType.START, null, 0);
            graphBlockStart.getEdges().add(edgeFlow);
            graphEdge.map(edgeFlow);
        }

        // map the last block's start to the program end (if such end is possible)
        if (graphBlocksLimit > 0) {
            var graphBlockEnd = graphBlocks.get(graphBlocks.size() - 1);
            if (graphBlockEnd.getGraphNodes().stream().noneMatch(graphNode -> graphNode.isInstruction(GUARANTEED_ENDS))) {
                var graphNode = graphBlockEnd.getGraphNodes().get(0);
                var edgeFlow = new EdgeFlow(EdgeFlowType.END, graphNode.getLine(), null);
                graphBlockEnd.getEdges().add(edgeFlow);
                graphEdge.map(edgeFlow);
            }
        }

        graph.getEdges().add(graphEdge);
    }
}
