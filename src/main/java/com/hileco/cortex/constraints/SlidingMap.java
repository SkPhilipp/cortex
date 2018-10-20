package com.hileco.cortex.constraints;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class SlidingMap<T> {

    private final Map<Integer, T> values = new HashMap<>();
    private int offset;

    public void set(int position, T value) {
        this.values.put(this.offset + position, value);
    }

    public T remove(int position) {
        return this.values.remove(this.offset + position);
    }

    public void forward() {
        this.offset++;
    }

    public void backward() {
        this.offset--;
    }
}
