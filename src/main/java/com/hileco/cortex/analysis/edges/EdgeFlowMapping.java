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

    private final Map<Integer, Set<Integer>> jumpMapping;
    private final Map<Integer, GraphBlock> lineMapping;

    public EdgeFlowMapping() {
        this.jumpMapping = new HashMap<>();
        this.lineMapping = new HashMap<>();
    }

    public void putLineMapping(Integer key, GraphBlock value) {
        this.lineMapping.put(key, value);
    }

    public void putJumpMapping(Integer source, Integer target) {
        this.jumpMapping.computeIfAbsent(source, ignore -> new HashSet<>())
                .add(target);
    }
}
