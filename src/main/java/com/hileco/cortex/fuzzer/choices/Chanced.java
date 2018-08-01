package com.hileco.cortex.fuzzer.choices;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public interface Chanced {

    static <T extends Chanced> Supplier<T> atRandom(T[] types) {
        Random random = new Random();
        double total = Arrays.stream(types).mapToDouble(Chanced::chance).sum();
        return () -> {
            double choice = random.nextDouble() * total;
            T selected = null;
            for (T type : types) {
                selected = type;
                choice = choice - type.chance();
                if (choice <= 0) {
                    break;
                }
            }
            return selected;
        };
    }

    double chance();
}
