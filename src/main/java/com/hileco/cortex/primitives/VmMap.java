package com.hileco.cortex.primitives;

public interface VmMap<K, V, T extends VmMap<K, V, T>> extends VmComponent<T> {
    V get(K key);

    void put(K key, V value);

    void remove(K key);
}