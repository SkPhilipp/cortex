package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;

public class KnownJumpIfProcessor implements Processor {
//    private void fully(GraphNode graphNode, Consumer<GraphNode> consumer) {
//        consumer.accept(graphNode);
//        for (var parameter : graphNode.getParameters()) {
//            this.fully(parameter, consumer);
//        }
//    }

    @Override
    public void process(Graph graph) {
// TODO: Implement generic known-partial method
//        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
//                .filter(graphNode -> graphNode.isInstruction(JUMP_IF.class))
//                .filter(graphNode -> graphNode.hasOneParameter(1, GraphNode::isSelfContained))
//                .forEach(jumpNode -> {
//                    var decidingNode = jumpNode.getParameters().get(1);
//                    var program = new Program(BigInteger.ZERO, decidingNode.toInstructions());
//                    var programContext = new ProgramContext(program);
//                    var processContext = new ProcessContext(programContext);
//                    var programRunner = new ProgramRunner(processContext);
//                    try {
//                        programRunner.run();
//                    } catch (ProgramException e) {
//                        throw new IllegalStateException("Unknown cause for ProgramException", e);
//                    }
//                    var result = programContext.getStack().peek();
//                    if (new BigInteger(result).compareTo(BigInteger.ZERO) > 0) {
//                        this.fully(decidingNode, node -> node.getInstruction().set(new NOOP()));
//                        jumpNode.getInstruction().set(new JUMP());
//                    } else {
//                        this.fully(jumpNode, node -> node.getInstruction().set(new NOOP()));
//                    }
//                }));
    }
}
