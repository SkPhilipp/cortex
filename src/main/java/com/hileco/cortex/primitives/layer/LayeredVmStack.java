package com.hileco.cortex.primitives.layer;

import com.hileco.vm.primitives.VmStack;

public class LayeredVmStack<T> implements VmStack<T, LayeredVmStack<T>> {
    private StackLayer<T> edge;

    public LayeredVmStack() {
        this(new StackLayer<>(null));
    }

    private LayeredVmStack(StackLayer<T> edge) {
        this.edge = edge;
    }

    public T peek() {
        return get(edge, edge.getLength() - 1);
    }

    private T get(StackLayer<T> layer, int index) {
        if (index >= layer.getLength()) {
            return null;
        }
        var layerOffset = layer.getLength() - layer.getEntries().size();
        var layerIndex = index - layerOffset;
        if (layerIndex >= 0 && layerIndex < layer.getEntries().size()) {
            return layer.getEntries().get(layerIndex);
        }
        var parent = layer.getParent();
        if (parent == null) {
            return null;
        }
        return get(parent, index);
    }

    public void push(T value) {
        edge.getEntries().push(value);
        edge.setLength(edge.getLength() + 1);
    }

    public T pop() {
        var value = peek();
        if (!edge.getEntries().isEmpty()) {
            edge.getEntries().pop();
        }
        var length = edge.getLength();
        if (length > 0) {
            edge.setLength(length - 1);
        }
        return value;
    }

    public int size() {
        return 0;
    }

    public LayeredVmStack<T> copy() {
        var child1 = new StackLayer<>(edge);
        var child2 = new StackLayer<>(edge);
        this.edge = child1;
        return new LayeredVmStack<>(child2);
    }

    public void close() {
        edge.close();
    }
}
