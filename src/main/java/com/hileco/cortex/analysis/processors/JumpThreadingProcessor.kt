package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph

class JumpThreadingProcessor : Processor {
    override fun process(graph: Graph) {
        //        graph.getGraphBlocks().stream()
        //                .filter(graphBlock -> graphBlock.countEntries() <= 1)
        //                .collect(Collectors.toList())
        //                .forEach(graph::mergeUpwards);
        //        graph.getGraphBlocks().stream()
        //                .filter(graphBlock -> graphBlock.countExits() <= 1)
        //                .collect(Collectors.toList())
        //                .forEach(graph::mergeDownwards);
    }
}
