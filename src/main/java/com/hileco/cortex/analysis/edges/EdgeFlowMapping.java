package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphBlock;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class EdgeFlowMapping implements Edge {
    public static final EdgeUtility<EdgeFlowMapping> UTIL = new EdgeUtility<>(EdgeFlowMapping.class);

    private final Map<Integer, Set<EdgeFlow>> flowsFromSource;
    private final Map<Integer, Set<EdgeFlow>> flowsToTarget;
    private final Map<Integer, GraphBlock> lineMapping;

    public EdgeFlowMapping() {
        this.flowsFromSource = new HashMap<>();
        this.flowsToTarget = new HashMap<>();
        this.lineMapping = new HashMap<>();
    }

    public void putLineMapping(Integer key, GraphBlock value) {
        this.lineMapping.put(key, value);
    }

    public void map(EdgeFlow edgeFlow) {
        this.flowsFromSource.computeIfAbsent(edgeFlow.getSource(), ignore -> new HashSet<>()).add(edgeFlow);
        this.flowsToTarget.computeIfAbsent(edgeFlow.getTarget(), ignore -> new HashSet<>()).add(edgeFlow);
    }
}
