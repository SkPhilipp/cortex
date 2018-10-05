package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;

public class ExitTrimProcessor implements Processor {
    @Override
    public void process(Graph graph) {
        //        graph.getGraphBlocks().stream()
        //                .filter(graphBlock -> graphBlock.countExits() == 0)
        //                // TODO: Trim dead code in replacement
        //                .forEach(graphBlock -> graph.replace(graphBlock, graphBlock));
    }
}
