package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;

import java.util.Random;
import java.util.function.Supplier;

public class ProgramFuzzer {

    public enum FuzzType {
        JUMP_TABLE,
        UNDERFLOW,
        OVERFLOW,
        CALL_BACK,
        COMPLEXITY,
        DIRECT_VALUE_TRANSFER,
        OPTIMIZABLE,
        STORAGE_WRITER,
        STORAGE_READER,
        STORAGE_MIXER,
        MATH_MIXER,
        BIT_MIXER,
        PROGRAM_PORTAL,
        RANDOM_JUMP_DESTINATION,
        JUMP_ANYWHERE,
        STACK_SWAPPER,
        INCREMENTING_LOOP,
        DECREMENTING_LOOP,
        STORE_INTO_LOAD;

        private static Supplier<FuzzType> randomized() {
            Random random = new Random();
            FuzzType[] choices = FuzzType.values();
            return () -> choices[random.nextInt(choices.length)];
        }
    }

    public LayeredMap<Integer, Program> generate() {
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        ProgramBuilder builder = programBuilderFactory.builder();
        LayeredMap<Integer, Program> programAtlas = new LayeredMap<>();
        programAtlas.put(0, builder.build());
        return programAtlas;
    }
}
