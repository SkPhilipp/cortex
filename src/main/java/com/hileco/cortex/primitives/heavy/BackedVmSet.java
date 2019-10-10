package com.hileco.cortex.primitives.heavy;


import com.hileco.vm.primitives.VmSet;

import java.util.HashSet;
import java.util.Set;

public class BackedVmSet<T> implements VmSet<T, BackedVmSet<T>> {
    private Set<T> backingSet;

    public BackedVmSet() {
        this.backingSet = new HashSet<>();
    }

    private BackedVmSet(Set<T> backingSet) {
        this.backingSet = new HashSet<>(backingSet);
    }

    public void close() {
    }

    public BackedVmSet<T> copy() {
        return new BackedVmSet<>(backingSet);
    }

    public boolean contains(T value) {
        return backingSet.contains(value);
    }

    public void add(T value) {
        backingSet.add(value);
    }

    public void remove(T value) {
        backingSet.remove(value);
    }

    public int size() {
        return backingSet.size();
    }
}