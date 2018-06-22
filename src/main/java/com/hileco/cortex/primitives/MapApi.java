package com.hileco.cortex.primitives;

import java.util.Set;

public interface MapApi<K, V, I extends MapApi<K, V, I>> {

    I  copy();

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    V get(K key);

    V put(K key, V value);

    V remove(K key);

    void clear();

    Set<K> keySet();

}
