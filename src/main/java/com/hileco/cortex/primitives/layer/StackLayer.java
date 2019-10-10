package com.hileco.cortex.primitives.layer;

import java.util.Stack;

public class StackLayer<T> extends Layer<StackLayer<T>> {
    private final Stack<T> entries;
    private int length;

    public StackLayer(StackLayer<T> parent) {
        super(parent);
        this.entries = new Stack<>();
        this.length = parent == null ? 0 : parent.length;
    }

    public Stack<T> getEntries() {
        return entries;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    boolean isEmpty() {
        var parent = getParent();
        if (parent != null) {
            return entries.isEmpty() && parent.length != this.length;
        } else {
            return entries.isEmpty();
        }
    }
}
