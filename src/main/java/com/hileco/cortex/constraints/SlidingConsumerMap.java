package com.hileco.cortex.constraints;

import java.util.function.Consumer;

public class SlidingConsumerMap<T> extends SlidingMap<Consumer<T>> {

    @Override
    public void set(int position, Consumer<T> value) {
        var existingConsumer = super.remove(position);
        super.set(position, existingConsumer == null ? value : value.andThen(existingConsumer));
    }

    @Override
    public Consumer<T> remove(int position) {
        var removed = super.remove(position);
        return removed != null ? removed : (expression) -> {
        };
    }

}
