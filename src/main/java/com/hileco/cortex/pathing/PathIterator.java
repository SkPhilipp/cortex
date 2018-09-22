package com.hileco.cortex.pathing;

import com.hileco.cortex.mapping.TreeMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PathIterator implements Iterator<List<Integer>> {

    private final TreeMapping treeMapping;
    private final List<PeekingIterator<Integer>> state;

    public PathIterator(TreeMapping treeMapping, Integer topSource) {
        this.treeMapping = treeMapping;
        this.state = new ArrayList<>();
        this.state.add(new PeekingIterator<>(List.of(topSource)));
        this.traverseDown();
    }

    @Override
    public boolean hasNext() {
        return !this.state.isEmpty();
    }

    private PeekingIterator<Integer> bottom() {
        if (this.state.isEmpty()) {
            return null;
        }
        return this.state.get(this.state.size() - 1);
    }

    private void iterate() {
        var bottom = this.bottom();
        while (bottom != null && !bottom.iterate()) {
            this.state.remove(this.state.size() - 1);
            bottom = this.bottom();
        }
        this.traverseDown();
    }

    private void traverseDown() {
        var bottom = this.bottom();
        while (bottom != null && this.treeMapping.getJumpMapping().containsKey(bottom.peek())) {
            var targets = this.treeMapping.getJumpMapping().get(bottom.peek());
            this.state.add(new PeekingIterator<>(targets));
            bottom = this.bottom();
        }
    }

    @Override
    public List<Integer> next() {
        var current = this.state.stream()
                .map(PeekingIterator::peek)
                .collect(Collectors.toList());
        this.iterate();
        return current;
    }

}
