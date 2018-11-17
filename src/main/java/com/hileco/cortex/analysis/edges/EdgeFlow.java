package com.hileco.cortex.analysis.edges;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class EdgeFlow implements Edge {
    public static final EdgeUtility<EdgeFlow> UTIL = new EdgeUtility<>(EdgeFlow.class);

    private EdgeFlowType type;
    private Integer source;
    private Integer target;

    @Override
    public String toString() {
        var sourceString = this.source == null ? "START" : this.source;
        var targetString = this.target == null ? "END" : this.target;
        return String.format("%s %s --> %s", this.type, sourceString, targetString);
    }
}
