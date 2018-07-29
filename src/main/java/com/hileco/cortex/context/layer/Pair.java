package com.hileco.cortex.context.layer;

import java.util.function.Function;

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public <T> Pair<K, T> mapValue(Function<V, T> function) {
        return new Pair<>(key, function.apply(value));
    }

    public <T> Pair<T, V> mapKey(Function<K, T> function) {
        return new Pair<>(function.apply(key), value);
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

}
