package com.hileco.cortex.primitives.layer;

import com.hileco.vm.primitives.VmMap;

public class LayeredVmMap<K, V> implements VmMap<K, V, LayeredVmMap<K, V>> {
    private MapLayer<K, V> edge;

    public LayeredVmMap() {
        this(new MapLayer<>(null));
    }

    private LayeredVmMap(MapLayer<K, V> edge) {
        this.edge = edge;
    }

    public V get(K key) {
        return get(edge, key);
    }

    private V get(MapLayer<K, V> layer, K key) {
        if (layer.getEntries().containsKey(key)) {
            return layer.getEntries().get(key);
        }
        if (layer.getDeletions().contains(key)) {
            return null;
        }
        var parent = layer.getParent();
        if (parent == null) {
            return null;
        }
        return get(parent, key);
    }

    public void put(K key, V value) {
        edge.getDeletions().remove(key);
        edge.getEntries().put(key, value);
    }

    public void remove(K key) {
        edge.getDeletions().add(key);
        edge.getEntries().remove(key);
    }

    public LayeredVmMap<K, V> copy() {
        while (edge != null && edge.isEmpty()) {
            this.edge = edge.getParent();
        }
        var child1 = new MapLayer<>(edge);
        var child2 = new MapLayer<>(edge);
        this.edge = child1;
        return new LayeredVmMap<>(child2);
    }

    public void close() {
        edge.close();
    }
}
