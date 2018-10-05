package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;

import java.util.stream.Collectors;

public class JumpUnreachableProcessor implements Processor {
    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().stream()
                .filter(graphBlock -> graphBlock.countEntries() == 0)
                .collect(Collectors.toList())
                .forEach(graph::remove);
    }
}
