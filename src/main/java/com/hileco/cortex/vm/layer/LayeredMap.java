package com.hileco.cortex.vm.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LayeredMap<K, V> implements MapApi<K, V, LayeredMap<K, V>> {
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

    @Override
    public LayeredMap<K, V> copy() {
        if (!this.layer.isEmpty() || !this.deletions.isEmpty()) {
            this.parent = new LayeredMap<>(this.parent, this.layer, this.deletions);
            this.layer = new HashMap<>();
            this.deletions = new HashSet<>();
        }
        return new LayeredMap<>(this.parent, new HashMap<>(), new HashSet<>());
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return this.layer.isEmpty() && (this.parent == null || this.parent.isEmpty());
    }

    @Override
    public boolean containsKey(K key) {
        return this.layer.containsKey(key) || (this.parent != null && this.parent.containsKey(key) && !this.deletions.contains(key));
    }

    @Override
    public V get(K key) {
        if (this.layer.containsKey(key)) {
            return this.layer.get(key);
        }
        if (this.parent != null && !this.deletions.contains(key)) {
            return this.parent.get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        var previous = this.get(key);
        this.deletions.remove(key);
        this.layer.put(key, value);
        return previous;
    }

    public <K2 extends K, V2 extends V> void merge(LayeredMap<K2, V2> map) {
        map.keySet().forEach(key -> {
            var value = map.get(key);
            this.put(key, value);
        });
    }

    @Override
    public V remove(K key) {
        var previous = this.get(key);
        this.layer.remove(key);
        if (this.parent != null && this.parent.containsKey(key)) {
            this.deletions.add(key);
        }
        return previous;
    }

    @Override
    public void clear() {
        this.parent = null;
        this.layer.clear();
        this.deletions.clear();
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<>();
        if (this.parent != null) {
            keys.addAll(this.parent.keySet());
        }
        keys.addAll(this.layer.keySet());
        if (this.parent != null) {
            keys.removeAll(this.deletions);
        }
        return keys;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("LayeredMap{");
        stringBuilder.append("\n");
        this.keySet().forEach(key -> {
            stringBuilder.append("[");
            stringBuilder.append(key);
            stringBuilder.append("] = ");
            stringBuilder.append(this.get(key));
            stringBuilder.append(",");
            stringBuilder.append("\n");
        });
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
