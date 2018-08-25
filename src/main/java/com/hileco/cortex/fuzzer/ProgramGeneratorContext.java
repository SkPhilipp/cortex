package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.InstructionsBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ProgramGeneratorContext {
    private final LayeredStack<InstructionsBuilder> instructionsBuilders;
    private final LayeredMap<BigInteger, Program> atlas;
    private final Random random;
    private final Supplier<FuzzProgram> randomFuzzProgramLayout;
    private final Supplier<FuzzFunction> randomFuzzFunctionLayout;
    private final Supplier<FuzzExpression> randomFuzzExpression;

    public ProgramGeneratorContext(long seed) {
        this.instructionsBuilders = new LayeredStack<>();
        this.atlas = new LayeredMap<>();
        this.random = new Random(seed);
        this.randomFuzzProgramLayout = this.of(FuzzProgram.values());
        this.randomFuzzFunctionLayout = this.of(FuzzFunction.values());
        this.randomFuzzExpression = this.of(FuzzExpression.values());
    }

    private <T extends Chanced> Supplier<T> of(T[] types) {
        var total = Arrays.stream(types).mapToDouble(Chanced::chance).sum();
        return () -> {
            var choice = this.random.nextDouble() * total;
            T selected = null;
            for (var type : types) {
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
        return BigInteger.valueOf(this.randomIntBetween(minimum, maximum));
    }

    public BigInteger random() {
        return BigInteger.valueOf(this.random.nextLong());
    }

    public int randomIntBetween(int minimum, int maximum) {
        return this.random.nextInt(maximum - minimum) + minimum;
    }

    public void forRandom(int minimum, int maximum, IntConsumer consumer) {
        var choice = this.randomIntBetween(minimum, maximum);
        IntStream.range(minimum, choice + 1).forEach(consumer);
    }

    public LayeredMap<BigInteger, Program> atlas() {
        return this.atlas;
    }

    public FuzzProgram randomFuzzProgram() {
        return this.randomFuzzProgramLayout.get();
    }

    public FuzzFunction randomFuzzFunction() {
        return this.randomFuzzFunctionLayout.get();
    }

    public FuzzExpression randomFuzzExpression() {
        return this.randomFuzzExpression.get();
    }

    public void pushBuilder(InstructionsBuilder value) {
        this.instructionsBuilders.push(value);
    }

    public void popBuilder() {
        this.instructionsBuilders.pop();
    }

    public InstructionsBuilder currentBuilder() {
        return this.instructionsBuilders.peek();
    }
}
