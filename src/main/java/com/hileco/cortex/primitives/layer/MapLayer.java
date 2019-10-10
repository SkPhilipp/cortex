package com.hileco.cortex.primitives.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapLayer<K, V> extends Layer<MapLayer<K, V>> {
    private final Map<K, V> entries;
    private final Set<K> deletions;

    public MapLayer(MapLayer<K, V> parent) {
        super(parent);
        this.entries = new HashMap<>();
        this.deletions = new HashSet<>();
    }

    public Map<K, V> getEntries() {
        return entries;
    }

    public Set<K> getDeletions() {
        return deletions;
    }

    @Override
    boolean isEmpty() {
        return entries.isEmpty() && deletions.isEmpty();
    }
}
