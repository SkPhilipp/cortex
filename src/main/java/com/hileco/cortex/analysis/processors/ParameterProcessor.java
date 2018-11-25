package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer;
import com.hileco.cortex.analysis.edges.EdgeParameters;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.SWAP;
import com.hileco.cortex.vm.layer.LayeredStack;

import java.util.ArrayList;


public class ParameterProcessor implements Processor {

    @Override
    public void process(Graph graph) {
        EdgeParameterConsumer.UTIL.clear(graph);
        EdgeParameters.UTIL.clear(graph);

        graph.getGraphBlocks().forEach(graphBlock -> {
            LayeredStack<GraphNode> stack = new LayeredStack<>();
            var graphNodes = graphBlock.getGraphNodes();
            for (var graphNode : graphNodes) {
                var instruction = graphNode.getInstruction().get();
                if (instruction instanceof JUMP_DESTINATION || instruction instanceof SWAP) {
                    stack.clear();
                    continue;
                }
                if (instruction instanceof DUPLICATE) {
                    stack.clear();
                    stack.push(graphNode);
                    continue;
                }
                var stackTakes = instruction.getStackParameters().size();
                if (stackTakes > 0) {
                    var parameters = new ArrayList<GraphNode>();
                    var stackSize = stack.size();
                    var totalMissing = stackTakes - stackSize;
                    for (var i = 0; i < totalMissing; i++) {
                        parameters.add(null);
                    }
                    var remainingMissing = Math.min(stackTakes, stackTakes - totalMissing);
                    for (var i = 0; i < remainingMissing; i++) {
                        var parameter = stack.get(stackSize - i);
                        parameter.getEdges().add(new EdgeParameterConsumer(graphNode));
                        parameters.add(parameter);
                    }
                    graphNode.getEdges().add(new EdgeParameters(parameters));
                    stack.clear();
                }
                if (!instruction.getStackAdds().isEmpty()) {
                    stack.push(graphNode);
                }
            }
        });
    }
}
