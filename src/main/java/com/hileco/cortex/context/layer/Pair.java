package com.hileco.cortex.context.layer;

import lombok.Value;

@Value
public class Pair<K, V> {
    private K key;
    private V value;
}
