package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramBuilderFactory;

import java.math.BigInteger;

public class ProgramGenerator {

    private static final int LIMIT_INITIAL_PROGRAMS = 10;

    private final ProgramBuilderFactory programBuilderFactory;

    public ProgramGenerator() {
        programBuilderFactory = new ProgramBuilderFactory();
    }

    public LayeredMap<BigInteger, Program> generate() {
        ProgramGeneratorContext context = new ProgramGeneratorContext();
        context.forRandom(1, LIMIT_INITIAL_PROGRAMS, i -> {
            context.pushBuilder(programBuilderFactory.builder());
            context.randomFuzzProgram().accept(context);
            Program generated = context.builder().build(context.random());
            context.atlas().put(generated.getAddress(), generated);
            context.popBuilder();
        });
        return context.atlas();
    }
}
