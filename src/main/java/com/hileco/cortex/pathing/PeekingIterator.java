package com.hileco.cortex.pathing;


import java.util.Collection;
import java.util.Iterator;

public class PeekingIterator<T> {
    private final Iterator<T> iterator;
    private T current;

    public PeekingIterator(Collection<T> options) {
        this.iterator = options.iterator();
        this.iterate();
    }

    /**
     * @return true when another element is available
     */
    public boolean iterate() {
        if (this.iterator.hasNext()) {
            this.current = this.iterator.next();
            return true;
        } else {
            this.current = null;
            return false;
        }
    }

    public T peek() {
        return this.current;
    }
}
