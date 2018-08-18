package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.InstructionsBuilder;

import java.math.BigInteger;

public class ProgramGenerator {

    private static final int LIMIT_INITIAL_PROGRAMS = 10;

    public LayeredMap<BigInteger, Program> generate(long seed) {
        ProgramGeneratorContext context = new ProgramGeneratorContext(seed);
        context.forRandom(1, LIMIT_INITIAL_PROGRAMS, i -> {
            context.pushBuilder(new InstructionsBuilder());
            context.randomFuzzProgram().accept(context);
            BigInteger address = context.random();
            Program generated = new Program(address, context.currentBuilder().build());
            context.atlas().put(address, generated);
            context.popBuilder();
        });
        return context.atlas();
    }
}
