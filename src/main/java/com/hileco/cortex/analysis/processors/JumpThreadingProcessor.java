package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;

import java.util.stream.Collectors;

public class JumpThreadingProcessor implements Processor {
    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().stream()
                .filter(graphBlock -> graphBlock.countEntries() <= 1)
                .collect(Collectors.toList())
                .forEach(graph::mergeUpwards);
        graph.getGraphBlocks().stream()
                .filter(graphBlock -> graphBlock.countExits() <= 1)
                .collect(Collectors.toList())
                .forEach(graph::mergeDownwards);
    }
}
