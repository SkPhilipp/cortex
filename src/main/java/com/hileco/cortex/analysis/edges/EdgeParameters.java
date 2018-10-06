package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

import java.util.List;

@Value
public class EdgeParameters implements Edge {
    public static final EdgeUtility<EdgeParameters> UTIL = new EdgeUtility<>(EdgeParameters.class);

    private List<GraphNode> graphNodes;
}
