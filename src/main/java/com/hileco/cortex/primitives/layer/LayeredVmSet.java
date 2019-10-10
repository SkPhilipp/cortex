package com.hileco.cortex.primitives.layer;

import com.hileco.vm.primitives.VmSet;

public class LayeredVmSet<T> implements VmSet<T, LayeredVmSet<T>> {
    private SetLayer<T> edge;

    public LayeredVmSet() {
        this(new SetLayer<>(null));
    }

    private LayeredVmSet(SetLayer<T> edge) {
        this.edge = edge;
    }

    public boolean contains(T key) {
        return contains(edge, key);
    }

    private boolean contains(SetLayer<T> layer, T key) {
        if (layer.getEntries().contains(key)) {
            return true;
        }
        if (layer.getDeletions().contains(key)) {
            return false;
        }
        var parent = layer.getParent();
        if (parent == null) {
            return false;
        }
        return contains(parent, key);
    }

    public void add(T key) {
        if (!contains(key)) {
            edge.getDeletions().remove(key);
            edge.getEntries().add(key);
        }
    }

    public void remove(T key) {
        if (contains(key)) {
            edge.getDeletions().add(key);
            edge.getEntries().remove(key);
        }
    }

    public int size() {
        throw new UnsupportedOperationException();
    }

    public LayeredVmSet<T> copy() {
        while (edge != null && edge.isEmpty()) {
            this.edge = edge.getParent();
        }
        var child1 = new SetLayer<>(edge);
        var child2 = new SetLayer<>(edge);
        this.edge = child1;
        return new LayeredVmSet<>(child2);
    }

    public void close() {
        edge.close();
    }
}
