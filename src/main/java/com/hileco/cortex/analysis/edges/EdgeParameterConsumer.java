package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

import java.util.List;

@Value
public class EdgeParameterConsumer implements Edge {
    private List<GraphNode> graphNodes;
}
