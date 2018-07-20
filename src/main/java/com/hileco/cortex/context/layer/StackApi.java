package com.hileco.cortex.context.layer;

import java.util.List;

public interface StackApi<T, I extends StackApi<T, I>> extends List<T>{

    I copy();

    void push(T value);

    T pop();

    T get(int index);

    void swap(int topOffsetLeft, int topOffsetRight);

    void duplicate(int topOffset);
}
