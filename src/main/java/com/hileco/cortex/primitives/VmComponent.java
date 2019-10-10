package com.hileco.cortex.primitives;

public interface VmComponent<T extends VmComponent<T>> {
    void close();

    T copy();
}
