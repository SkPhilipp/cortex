package com.hileco.cortex.primitives.layer;

import java.util.ArrayList;
import java.util.List;

public abstract class Layer<T extends Layer<T>> {
    private final List<T> children;
    private T parent;

    public Layer(T parent) {
        this.children = new ArrayList<>();
        this.parent = parent;
        while (this.parent != null && this.parent.isEmpty()) {
            this.parent = this.parent.getParent();
        }
        if (this.parent != null) {
            this.parent.getChildren().add((T) this);
        }
    }

    public List<T> getChildren() {
        return children;
    }

    public T getParent() {
        return parent;
    }

    abstract boolean isEmpty();

    public void close() {
        var currentParent = this.parent;
        while (currentParent != null) {
            currentParent.getChildren().remove(this);
            if (currentParent.getChildren().isEmpty()) {
                currentParent = currentParent.getParent();
            }
        }
        this.parent = null;
    }
}
