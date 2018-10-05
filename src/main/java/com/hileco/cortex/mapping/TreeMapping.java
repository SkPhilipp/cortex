package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.GraphBlock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreeMapping {
    private final Map<Integer, Set<Integer>> jumpMapping;
    private final Map<Integer, GraphBlock> lineMapping;

    public TreeMapping() {
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

    public Map<Integer, Set<Integer>> getJumpMapping() {
        return this.jumpMapping;
    }

    public Map<Integer, GraphBlock> getLineMapping() {
        return this.lineMapping;
    }
}
