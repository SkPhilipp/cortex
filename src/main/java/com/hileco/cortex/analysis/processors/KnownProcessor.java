package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphNode;
import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class KnownProcessor implements Processor {

    private void noopDownwards(GraphNode graphNode) {
        graphNode.getInstruction().set(new NOOP());
        graphNode.getParameters().forEach(this::noopDownwards);
    }

    @Override
    public void process(Graph graph) {
        // TODO: Parameters could also be selfContained.
        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                .filter(GraphNode::isSelfContained)
                .forEach(graphNode -> {
                    var program = new Program(BigInteger.ZERO, graphNode.toInstructions());
                    var programContext = new ProgramContext(program);
                    var processContext = new ProcessContext(programContext);
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
                    }
                    // TODO: Replace the entire graphNode also when more instructions are available...
                }));
    }
}
