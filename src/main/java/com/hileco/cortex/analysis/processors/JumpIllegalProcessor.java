package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;

import java.util.stream.Collectors;

import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;

public class JumpIllegalProcessor implements Processor {

    @Override
    public void process(Graph graph) {
        EdgeFlowMapping.UTIL.findAny(graph).ifPresent(edgeFlowMapping -> edgeFlowMapping.getFlowsFromSource().forEach((sourceLine, edgeFlows) -> {
            var jumpEdgeFlows = edgeFlows.stream()
                    .filter(edgeFlow -> edgeFlow.getType() == INSTRUCTION_JUMP)
                    .collect(Collectors.toSet());
            if (jumpEdgeFlows.size() == 1) {
                var onlyJumpEdgeFlow = jumpEdgeFlows.stream().findFirst().orElseThrow();
                var targetedNode = edgeFlowMapping.getNodeLineMapping().get(onlyJumpEdgeFlow.getTarget());
                var sourceNode = edgeFlowMapping.getNodeLineMapping().get(onlyJumpEdgeFlow.getSource());
                if (targetedNode == null) {
                    sourceNode.getInstruction().set(new HALT(JUMP_OUT_OF_BOUNDS));
                } else if (!(targetedNode.getInstruction().get() instanceof JUMP_DESTINATION)) {
                    sourceNode.getInstruction().set(new HALT(JUMP_TO_ILLEGAL_INSTRUCTION));
                }
            }
        }));
    }
}
