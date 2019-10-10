package com.hileco.cortex.primitives;

public interface VmStack<V, T extends VmStack<V, T>> extends VmComponent<T> {
    V peek();

    void push(V value);

    V pop();

    int size();
}