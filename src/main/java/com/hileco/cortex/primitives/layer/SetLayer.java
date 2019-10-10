package com.hileco.cortex.primitives.layer;

import java.util.HashSet;
import java.util.Set;

public class SetLayer<T> extends Layer<SetLayer<T>> {
    private final Set<T> entries;
    private final Set<T> deletions;

    public SetLayer(SetLayer<T> parent) {
        super(parent);
        this.entries = new HashSet<>();
        this.deletions = new HashSet<>();
    }

    public Set<T> getEntries() {
        return entries;
    }

    public Set<T> getDeletions() {
        return deletions;
    }

    @Override
    boolean isEmpty() {
        return entries.isEmpty() && deletions.isEmpty();
    }
}
