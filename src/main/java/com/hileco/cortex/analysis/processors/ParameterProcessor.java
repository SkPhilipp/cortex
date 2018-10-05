package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.SWAP;

import static com.hileco.cortex.analysis.GraphNodeType.INSTRUCTION;
import static com.hileco.cortex.analysis.GraphNodeType.UNKNOWN;

public class ParameterProcessor implements Processor {
    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().forEach(graphBlock -> {
            LayeredStack<GraphNode> stack = new LayeredStack<>();
            var graphNodes = graphBlock.getGraphNodes();
            for (var node = 0; node < graphNodes.size(); node++) {
                var graphNode = graphNodes.get(node);
                var instruction = graphNode.getInstruction().get();
                if (graphNode.getType() != INSTRUCTION
                        || instruction instanceof JUMP_DESTINATION
                        || instruction instanceof SWAP) {
                    stack.clear();
                    continue;
                }
                if (instruction instanceof DUPLICATE) {
                    stack.clear();
                    stack.push(graphNode);
                    continue;
                }
                var stackTakes = instruction.getStackTakes();
                var limit = stack.size();
                for (var i = 0; i < stackTakes.size(); i++) {
                    GraphNode parameter;
                    if (i < limit) {
                        parameter = stack.pop();
                        node--;
                    } else {
                        parameter = new GraphNode();
                        parameter.setType(UNKNOWN);
                    }
                    graphNode.getParameters().add(parameter);
                    graphNodes.remove(parameter);
                }
                if (!instruction.getStackAdds().isEmpty()) {
                    stack.push(graphNode);
                }
            }
        });
    }
}
