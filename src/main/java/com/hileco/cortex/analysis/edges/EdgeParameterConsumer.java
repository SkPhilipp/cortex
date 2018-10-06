package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

@Value
public class EdgeParameterConsumer implements Edge {
    public static final EdgeUtility<EdgeParameterConsumer> UTIL = new EdgeUtility<>(EdgeParameterConsumer.class);

    private GraphNode graphNode;
}
