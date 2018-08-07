package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ProgramGeneratorContext {
    private final LayeredStack<ProgramBuilder> programBuilders;
    private final LayeredMap<BigInteger, Program> atlas;
    private final Random random;
    private final Supplier<FuzzProgram> randomFuzzProgramLayout;
    private final Supplier<FuzzFunction> randomFuzzFunctionLayout;
    private final Supplier<FuzzExpression> randomFuzzExpression;

    public ProgramGeneratorContext(long seed) {
        programBuilders = new LayeredStack<>();
        atlas = new LayeredMap<>();
        random = new Random(seed);
        randomFuzzProgramLayout = of(FuzzProgram.values());
        randomFuzzFunctionLayout = of(FuzzFunction.values());
        randomFuzzExpression = of(FuzzExpression.values());
    }

    private <T extends Chanced> Supplier<T> of(T[] types) {
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

    public BigInteger randomBetween(int minimum, int maximum) {
        return BigInteger.valueOf(randomIntBetween(minimum, maximum));
    }

    public BigInteger random() {
        return BigInteger.valueOf(random.nextLong());
    }

    public int randomIntBetween(int minimum, int maximum) {
        return random.nextInt(maximum - minimum) + minimum;
    }

    public void forRandom(int minimum, int maximum, IntConsumer consumer) {
        int choice = randomIntBetween(minimum, maximum);
        IntStream.range(minimum, choice + 1).forEach(consumer);
    }

    public LayeredMap<BigInteger, Program> atlas() {
        return atlas;
    }

    public FuzzProgram randomFuzzProgram() {
        return randomFuzzProgramLayout.get();
    }

    public FuzzFunction randomFuzzFunction() {
        return randomFuzzFunctionLayout.get();
    }

    public FuzzExpression randomFuzzExpression() {
        return randomFuzzExpression.get();
    }

    public void pushBuilder(ProgramBuilder value) {
        programBuilders.push(value);
    }

    public ProgramBuilder popBuilder() {
        return programBuilders.pop();
    }

    public ProgramBuilder builder() {
        return programBuilders.peek();
    }
}
