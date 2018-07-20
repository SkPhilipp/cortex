package com.hileco.cortex.context.layer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class LayeredStack<V> implements StackApi<V, LayeredStack<V>> {
    private LayeredStack<V> parent;
    private Map<Integer, V> layer;
    private int size;

    public LayeredStack() {
        parent = null;
        layer = new HashMap<>();
        size = 0;
    }

    private LayeredStack(LayeredStack<V> parent, Map<Integer, V> layer, int size) {
        this.parent = parent;
        this.layer = layer;
        this.size = size;
    }

    @Override
    public synchronized LayeredStack<V> copy() {
        if (layer.size() > 0 || size != parent.size) {
            parent = new LayeredStack<>(parent, layer, size);
            layer = new HashMap<>();
            size = parent.size;
        }
        return new LayeredStack<>(parent, new HashMap<>(), parent.size);
    }

    @Override
    public synchronized void push(V value) {
        size++;
        layer.put(size, value);
    }

    @Override
    public synchronized V pop() {
        V removed = layer.remove(size);
        size--;
        return removed;
    }

    private UnsupportedOperationException arbitraryModification() {
        return new UnsupportedOperationException("Modifying elements at arbitrary positions is not supported.");
    }

    private synchronized void checkBounds(int index) {
        if (size < index) {
            throw new IndexOutOfBoundsException(String.format("Size %s < Index %s", size, index));
        }
    }

    @Override
    public synchronized V get(int index) {
        checkBounds(index);
        if (layer.containsKey(index)) {
            return layer.get(index);
        } else {
            return parent.get(index);
        }
    }

    @Override
    public synchronized void swap(int topOffsetLeft, int topOffsetRight) {
        int indexA = this.size - topOffsetLeft;
        int indexB = this.size - topOffsetRight;
        checkBounds(indexA);
        checkBounds(indexB);
        V valueA = get(indexA);
        V valueB = get(indexB);
        layer.put(indexA, valueB);
        layer.put(indexB, valueA);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return layer.size() == 0 && (parent == null || parent.size == 0);
    }

    @Override
    public synchronized void duplicate(int topOffset) {
        int index = this.size - topOffset;
        checkBounds(index);
        V value = get(index);
        push(value);
    }

    @Override
    public synchronized boolean contains(Object o) {
        if (o instanceof Integer) {
            return this.layer.containsKey(o) || (parent != null && parent.contains(o));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private class IndexedIterator implements ListIterator<V> {
        private int index;

        IndexedIterator(int index) {
            this.index = index;
        }

        @Override
        public synchronized boolean hasNext() {
            return index <= LayeredStack.this.size;
        }

        @Override
        public synchronized V next() {
            if (index > size) {
                throw new NoSuchElementException();
            }
            V value = LayeredStack.this.get(index);
            index++;
            return value;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public V previous() {
            if (index == 0) {
                throw new NoSuchElementException();
            }
            index--;
            return LayeredStack.this.get(index);
        }

        @Override
        public int nextIndex() {
            return Math.min(index, size - 1);
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            throw arbitraryModification();
        }

        @Override
        public void set(V v) {
            throw arbitraryModification();
        }

        @Override
        public void add(V v) {
            throw arbitraryModification();
        }
    }

    @Override
    public IndexedIterator iterator() {
        return new IndexedIterator(1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized V[] toArray() {
        Object[] objects = new Object[size];
        for (int index = 0; index < size; index++) {
            V value = LayeredStack.this.get(index + 1);
            objects[index] = value;
        }
        return (V[]) objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) toArray();
        } else {
            for (int index = 0; index < a.length; index++) {
                V value = LayeredStack.this.get(index + 1);
                a[index] = (T) value;
            }
            return a;
        }
    }

    @Override
    public synchronized boolean add(V v) {
        push(v);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw arbitraryModification();
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends V> c) {
        c.forEach(this::push);
        return true;
    }

    @Override
    public boolean addAll(int offset, Collection<? extends V> c) {
        throw arbitraryModification();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw arbitraryModification();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw arbitraryModification();
    }

    @Override
    public synchronized void clear() {
        this.layer.clear();
        this.size = 0;
    }

    @Override
    public V set(int index, V element) {
        throw arbitraryModification();
    }

    @Override
    public void add(int index, V element) {
        throw arbitraryModification();
    }

    @Override
    public V remove(int index) {
        throw arbitraryModification();
    }

    @Override
    public synchronized int indexOf(Object o) {
        IndexedIterator iterator = new IndexedIterator(1);
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next(), o)) {
                return iterator.index - 1;
            }
        }
        return -1;
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        IndexedIterator iterator = new IndexedIterator(1);
        int match = -1;
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next(), o)) {
                match = iterator.index - 1;
            }
        }
        return match;
    }

    @Override
    public ListIterator<V> listIterator() {
        return new IndexedIterator(1);
    }

    @Override
    public ListIterator<V> listIterator(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
        return new IndexedIterator(index);
    }

    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Creating in-memory sub-lists is currently not supported.");
    }

    @Override
    public String toString() {
        return "LayeredStack{" +
                "parent=" + parent +
                ", layer=" + layer +
                ", size=" + size +
                '}';
    }
}
