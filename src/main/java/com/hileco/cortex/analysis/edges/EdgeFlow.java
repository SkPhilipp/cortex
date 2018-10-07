package com.hileco.cortex.analysis.edges;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class EdgeFlow implements Edge {
    public static final EdgeUtility<EdgeFlow> UTIL = new EdgeUtility<>(EdgeFlow.class);

    private EdgeFlowType type;
    private Integer target;
}
