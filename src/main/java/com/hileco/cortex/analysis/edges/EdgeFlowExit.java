package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

@Value
public class EdgeFlowExit implements Edge {
    private EdgeFlowType type;
    private GraphNode source;
    private GraphNode target;
}
