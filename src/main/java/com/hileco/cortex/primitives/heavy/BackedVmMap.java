package com.hileco.cortex.primitives.heavy;


import com.hileco.vm.primitives.VmMap;

import java.util.HashMap;
import java.util.Map;

public class BackedVmMap<K, V> implements VmMap<K, V, BackedVmMap<K, V>> {
    private Map<K, V> backingMap;

    public BackedVmMap() {
        this.backingMap = new HashMap<>();
    }

    private BackedVmMap(Map<K, V> backingMap) {
        this.backingMap = new HashMap<>(backingMap);
    }

    public void close() {
    }

    public BackedVmMap<K, V> copy() {
        return new BackedVmMap<>(backingMap);
    }

    public V get(K key) {
        return backingMap.get(key);
    }

    public void put(K key, V value) {
        backingMap.put(key, value);
    }

    public void remove(K key) {
        backingMap.remove(key);
    }
}


