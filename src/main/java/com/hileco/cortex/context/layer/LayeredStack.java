package com.hileco.cortex.context.layer;

import java.util.Arrays;
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
        this.parent = null;
        this.layer = new HashMap<>();
        this.size = 0;
    }

    private LayeredStack(LayeredStack<V> parent, Map<Integer, V> layer, int size) {
        this.parent = parent;
        this.layer = layer;
        this.size = size;
    }

    @Override
    public synchronized LayeredStack<V> copy() {
        if (this.layer.size() > 0 || this.size != this.parent.size) {
            this.parent = new LayeredStack<>(this.parent, this.layer, this.size);
            this.layer = new HashMap<>();
            this.size = this.parent.size;
        }
        return new LayeredStack<>(this.parent, new HashMap<>(), this.parent.size);
    }

    @Override
    public synchronized void push(V value) {
        this.size++;
        this.layer.put(this.size, value);
    }

    @Override
    public synchronized V pop() {
        V removed = this.layer.remove(this.size);
        this.size--;
        return removed;
    }

    public synchronized V peek() {
        return this.layer.get(this.size);
    }

    private UnsupportedOperationException arbitraryModification() {
        return new UnsupportedOperationException("Modifying elements at arbitrary positions is not supported.");
    }

    private synchronized void checkBounds(int index) {
        if (this.size < index) {
            throw new IndexOutOfBoundsException(String.format("Size %s < Index %s", this.size, index));
        }
    }

    @Override
    public synchronized V get(int index) {
        this.checkBounds(index);
        if (this.layer.containsKey(index)) {
            return this.layer.get(index);
        } else {
            if (this.parent == null) {
                throw new IndexOutOfBoundsException(String.format("Size %s < Index %s", this.size, index));
            }
            return this.parent.get(index);
        }
    }

    @Override
    public synchronized void swap(int topOffsetLeft, int topOffsetRight) {
        int indexA = this.size - topOffsetLeft;
        int indexB = this.size - topOffsetRight;
        this.checkBounds(indexA);
        this.checkBounds(indexB);
        V valueA = this.get(indexA);
        V valueB = this.get(indexB);
        this.layer.put(indexA, valueB);
        this.layer.put(indexB, valueA);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.layer.size() == 0 && (this.parent == null || this.parent.size == 0);
    }

    @Override
    public synchronized void duplicate(int topOffset) {
        int index = this.size - topOffset;
        this.checkBounds(index);
        V value = this.get(index);
        this.push(value);
    }

    @Override
    public synchronized boolean contains(Object o) {
        if (o instanceof Integer) {
            return this.layer.containsKey(o) || (this.parent != null && this.parent.contains(o));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public IndexedIterator iterator() {
        return new IndexedIterator(1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized V[] toArray() {
        Object[] objects = new Object[this.size];
        for (int index = 0; index < this.size; index++) {
            V value = this.get(index + 1);
            objects[index] = value;
        }
        return (V[]) objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> T[] toArray(T[] a) {
        if (a.length < this.size) {
            return (T[]) this.toArray();
        } else {
            for (int index = 0; index < a.length; index++) {
                V value = this.get(index + 1);
                a[index] = (T) value;
            }
            return a;
        }
    }

    @Override
    public synchronized boolean add(V v) {
        this.push(v);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw this.arbitraryModification();
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
        throw this.arbitraryModification();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw this.arbitraryModification();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw this.arbitraryModification();
    }

    @Override
    public synchronized void clear() {
        this.layer.clear();
        this.size = 0;
    }

    @Override
    public V set(int index, V element) {
        throw this.arbitraryModification();
    }

    @Override
    public void add(int index, V element) {
        throw this.arbitraryModification();
    }

    @Override
    public V remove(int index) {
        throw this.arbitraryModification();
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
        if (index > this.size) {
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LayeredStack{");
        IndexedIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        LayeredStack<?> that = (LayeredStack<?>) o;
        return this.size == that.size &&
                Arrays.equals(this.toArray(), that.toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object[]) this.toArray());
    }

    private class IndexedIterator implements ListIterator<V> {
        private int index;

        IndexedIterator(int index) {
            this.index = index;
        }

        @Override
        public synchronized boolean hasNext() {
            return this.index <= LayeredStack.this.size;
        }

        @Override
        public synchronized V next() {
            if (this.index > LayeredStack.this.size) {
                throw new NoSuchElementException();
            }
            V value = LayeredStack.this.get(this.index);
            this.index++;
            return value;
        }

        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }

        @Override
        public V previous() {
            if (this.index == 0) {
                throw new NoSuchElementException();
            }
            this.index--;
            return LayeredStack.this.get(this.index);
        }

        @Override
        public int nextIndex() {
            return Math.min(this.index, LayeredStack.this.size - 1);
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw LayeredStack.this.arbitraryModification();
        }

        @Override
        public void set(V v) {
            throw LayeredStack.this.arbitraryModification();
        }

        @Override
        public void add(V v) {
            throw LayeredStack.this.arbitraryModification();
        }
    }
}
