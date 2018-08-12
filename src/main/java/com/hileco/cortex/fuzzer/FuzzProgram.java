package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.data.ProgramStoreZone;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum FuzzProgram implements Chanced, Consumer<ProgramGeneratorContext> {

    /**
     * A commonly used program layout; A jump table for each function of the program.
     */
    FUNCTION_TABLE(12, context -> {
        context.forRandom(1, Constants.LIMIT_INITIAL_CALL_DATA_LOAD, index -> {
            context.builder().PUSH(context.randomBetween(0, Constants.LIMIT_SIZE_CALL_DATA).toByteArray());
            context.builder().LOAD(ProgramStoreZone.CALL_DATA);
        });
        List<BigInteger> functions = new ArrayList<>();
        context.forRandom(1, Constants.LIMIT_INITIAL_FUNCTIONS, i -> functions.add(context.random()));
        functions.forEach(address -> {
            context.builder().PUSH(context.random().toByteArray());
            context.builder().EQUALS();
            context.builder().PUSH_LABEL(address.toString());
            context.builder().JUMP_IF();
        });
        context.builder().PUSH_LABEL(Constants.PROGRAM_END_LABEL);
        context.builder().JUMP();
        functions.forEach(address -> {
            context.builder().JUMP_DESTINATION_WITH_LABEL(address.toString());
            context.randomFuzzFunction().accept(context);
            context.builder().PUSH_LABEL(Constants.PROGRAM_END_LABEL);
            context.builder().JUMP();
        });
        context.builder().JUMP_DESTINATION_WITH_LABEL(Constants.PROGRAM_END_LABEL);
    }),

    FUNCTION(1D, context -> {
        context.forRandom(1, Constants.LIMIT_INITIAL_CALL_DATA_LOAD, index -> {
            context.builder().PUSH(context.randomBetween(0, Constants.LIMIT_SIZE_CALL_DATA).toByteArray());
            context.builder().LOAD(ProgramStoreZone.CALL_DATA);
        });
        context.randomFuzzFunction().accept(context);
    });

    interface Constants {
        int LIMIT_INITIAL_FUNCTIONS = 10;
        int LIMIT_INITIAL_CALL_DATA_LOAD = 10;
        int LIMIT_SIZE_CALL_DATA = 8192;
        String PROGRAM_END_LABEL = "end";
    }

    private double chance;
    private final Consumer<ProgramGeneratorContext> implementation;

    FuzzProgram(double chance, Consumer<ProgramGeneratorContext> implementation) {
        this.chance = chance;
        this.implementation = implementation;
    }

    @Override
    public double chance() {
        return chance;
    }

    @Override
    public void accept(ProgramGeneratorContext programGeneratorContext) {
        implementation.accept(programGeneratorContext);
    }
}
