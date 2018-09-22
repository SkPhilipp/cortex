package com.hileco.cortex.mapping;

import com.hileco.cortex.analysis.TreeBlock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreeMapping {
    private final Map<TreeBlock, Map<Integer, Set<Integer>>> jumpMappings;
    private final Map<Integer, TreeBlock> lineMapping;

    public TreeMapping() {
        this.jumpMappings = new HashMap<>();
        this.lineMapping = new HashMap<>();
    }

    public void putLineMapping(Integer key, TreeBlock value) {
        this.lineMapping.put(key, value);
    }

    public void putJumpMapping(TreeBlock key, Integer source, Integer target) {
        this.jumpMappings.computeIfAbsent(key, ignore -> new HashMap<>())
                .computeIfAbsent(source, ignore -> new HashSet<>())
                .add(target);
    }

    public Map<TreeBlock, Map<Integer, Set<Integer>>> getJumpMappings() {
        return this.jumpMappings;
    }

    public Map<Integer, TreeBlock> getLineMapping() {
        return this.lineMapping;
    }
}
