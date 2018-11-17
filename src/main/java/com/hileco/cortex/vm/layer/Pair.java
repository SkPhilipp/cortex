package com.hileco.cortex.vm.layer;

import lombok.Value;

@Value
public class Pair<K, V> {
    private K key;
    private V value;
}
