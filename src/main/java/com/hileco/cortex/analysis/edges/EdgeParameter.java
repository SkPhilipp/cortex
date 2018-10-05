package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

@Value
public class EdgeParameter implements Edge {
    private GraphNode graphNode;
}
