package com.hileco.cortex.primitives.test;

import java.util.Random;
import java.util.function.Consumer;

public class Variation {
    private Random random;

    public Variation(long seed) {
        this.random = new Random(seed);
    }

    public void seed(long seed) {
        this.random = new Random(seed);
    }

    public void maybe(Runnable runnable) {
        if (random.nextBoolean()) {
            runnable.run();
        }
    }

    public static void fuzzed(long times, Consumer<Variation> fuzzee) {
        for (var i = 0L; i < times; i++) {
            try {
                var variation = new Variation(i);
                fuzzee.accept(variation);
            } catch (Throwable t) {
                throw new IllegalStateException(String.format("Exception using variation seed %d of %d", i, times), t);
            }
        }
    }
}