package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.analysis.edges.EdgeParameters;
import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.Program;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class KnownProcessor implements Processor {

    private void noopDownwards(GraphNode graphNode) {
        this.unlinkParameters(graphNode);
//        var parameterConsumerEdges = graphNode.getEdges().stream()
//                .filter(EdgeParameterConsumer.class::isInstance)
//                .collect(Collectors.toList());
//        graphNode.getEdges().removeAll(parameterConsumerEdges);
        graphNode.getInstruction().set(new NOOP());
        graphNode.getParameters().forEach(this::noopDownwards);
    }

    private void unlinkParameters(GraphNode graphNode) {
        var parameterEdges = graphNode.getEdges().stream()
                .filter(EdgeParameters.class::isInstance)
                .collect(Collectors.toList());
        graphNode.getEdges().removeAll(parameterEdges);
    }

    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                .filter(graphNode -> EdgeParameters.UTIL.count(graphNode) > 0)
                .filter(GraphNode::isSelfContained)
                .forEach(graphNode -> {
                    var program = new Program(BigInteger.ZERO, graphNode.toInstructions());
                    var programContext = new ProgramContext(program);
                    var processContext = new VirtualMachine(programContext);
                    var programRunner = new ProgramRunner(processContext);
                    try {
                        programRunner.run();
                    } catch (ProgramException e) {
                        throw new IllegalStateException("Unknown cause for ProgramException", e);
                    }
                    var stack = programContext.getStack();
                    var instructions = stack.stream()
                            .map(PUSH::new)
                            .collect(Collectors.toList());
                    if (instructions.size() == 1) {
                        graphNode.getInstruction().set(instructions.get(0));
                        graphNode.getParameters().forEach(this::noopDownwards);
                        this.unlinkParameters(graphNode);
                    }
                    // TODO: Replace the entire graphNode also when more instructions are available...
                }));
    }
}
