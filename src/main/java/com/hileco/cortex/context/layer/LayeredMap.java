package com.hileco.cortex.context.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LayeredMap<K, V> implements MapApi<K, V, LayeredMap<K ,V>>{
    private LayeredMap<K, V> parent;
    private Map<K, V> layer;
    private Set<K> deletions;

    public LayeredMap() {
        this.parent = null;
        this.layer = new HashMap<>();
        this.deletions = new HashSet<>();
    }

    private LayeredMap(LayeredMap<K, V> parent, Map<K, V> layer, Set<K> deletions) {
        this.parent = parent;
        this.layer = layer;
        this.deletions = deletions;
    }

    public LayeredMap<K, V> copy() {
        if (layer.size() > 0 || deletions.size() > 0) {
            this.parent = new LayeredMap<>(parent, layer, deletions);
            this.layer = new HashMap<>();
            this.deletions = new HashSet<>();
        }
        return new LayeredMap<>(parent, new HashMap<>(), new HashSet<>());
    }

    public int size() {
        return this.keySet().size();
    }

    public boolean isEmpty() {
        return layer.isEmpty() && (parent == null || parent.isEmpty());
    }

    public boolean containsKey(K key) {
        return layer.containsKey(key) || (parent != null && parent.containsKey(key) && !deletions.contains(key));
    }

    public V get(K key) {
        if (layer.containsKey(key)) {
            return layer.get(key);
        }
        if (parent != null && !deletions.contains(key)) {
            return parent.get(key);
        }
        return null;
    }

    public V put(K key, V value) {
        V previous = get(key);
        deletions.remove(key);
        layer.put(key, value);
        return previous;
    }

    public V remove(K key) {
        V previous = get(key);
        layer.remove(key);
        if (parent != null && parent.containsKey(key)) {
            deletions.add(key);
        }
        return previous;
    }

    public void clear() {
        this.parent = null;
        this.layer.clear();
        this.deletions.clear();
    }

    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<>();
        if (parent != null) {
            keys.addAll(parent.keySet());
        }
        keys.addAll(layer.keySet());
        if (parent != null) {
            keys.removeAll(deletions);
        }
        return keys;
    }
}
