package com.hileco.cortex.primitives.heavy;

import com.hileco.vm.primitives.VmStack;

import java.util.ArrayList;
import java.util.List;

public class BackedVmStack<T> implements VmStack<T, BackedVmStack<T>> {
    private List<T> backingList;

    public BackedVmStack() {
        this.backingList = new ArrayList<>();
    }

    private BackedVmStack(List<T> backingList) {
        this.backingList = new ArrayList<>(backingList);
    }

    public void close() {
        backingList.clear();
    }

    public BackedVmStack<T> copy() {
        return new BackedVmStack<>(backingList);
    }

    public T peek() {
        return backingList.get(backingList.size() - 1);
    }

    public void push(T value) {
        backingList.add(value);
    }

    public T pop() {
        return backingList.remove(backingList.size() - 1);
    }

    public int size() {
        return backingList.size();
    }
}
