package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.stack.SWAP;

public class DeadSwapProcessor implements Processor {

    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                .filter(graphNode -> graphNode.isInstruction(SWAP.class))
                .filter(swapNode -> {
                    var swap = (SWAP) swapNode.getInstruction().get();
                    return swap.getPositionLeft() == swap.getPositionRight();
                })
                .forEach(swapNode -> {
                    swapNode.getInstruction().set(new NOOP());
                }));
    }
}
