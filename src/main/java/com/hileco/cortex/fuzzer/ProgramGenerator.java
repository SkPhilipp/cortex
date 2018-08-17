package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramBuilder;

import java.math.BigInteger;

public class ProgramGenerator {

    private static final int LIMIT_INITIAL_PROGRAMS = 10;

    public LayeredMap<BigInteger, Program> generate(long seed) {
        ProgramGeneratorContext context = new ProgramGeneratorContext(seed);
        context.forRandom(1, LIMIT_INITIAL_PROGRAMS, i -> {
            context.pushBuilder(new ProgramBuilder());
            context.randomFuzzProgram().accept(context);
            Program generated = context.builder().build(context.random());
            context.atlas().put(generated.getAddress(), generated);
            context.popBuilder();
        });
        return context.atlas();
    }
}
