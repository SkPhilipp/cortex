package com.hileco.cortex.primitives;

public interface VmSet<V, T extends VmSet<V, T>> extends VmComponent<T> {
    boolean contains(V value);

    void add(V value);

    void remove(V value);

    int size();
}