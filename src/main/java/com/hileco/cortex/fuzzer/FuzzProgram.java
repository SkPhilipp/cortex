package com.hileco.cortex.fuzzer;

import com.hileco.cortex.vm.data.ProgramStoreZone;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Consumer;

public enum FuzzProgram implements Chanced, Consumer<ProgramGeneratorContext> {

    /**
     * A commonly used program layout; A jump table for each function of the program.
     */
    FUNCTION_TABLE(12, context -> {
        context.forRandom(1, Constants.LIMIT_INITIAL_CALL_DATA_LOAD, index -> {
            context.currentBuilder().include(() -> new PUSH(context.randomBetween(0, Constants.LIMIT_SIZE_CALL_DATA).toByteArray()));
            context.currentBuilder().include(() -> new LOAD(ProgramStoreZone.CALL_DATA));
        });
        var functions = new ArrayList<BigInteger>();
        context.forRandom(1, Constants.LIMIT_INITIAL_FUNCTIONS, i -> functions.add(context.random()));
        functions.forEach(address -> {
            context.currentBuilder().include(() -> new PUSH(context.random().toByteArray()));
            context.currentBuilder().include(EQUALS::new);
            context.currentBuilder().PUSH_LABEL(address.toString());
            context.currentBuilder().include(JUMP_IF::new);
        });
        context.currentBuilder().PUSH_LABEL(Constants.PROGRAM_END_LABEL);
        context.currentBuilder().include(JUMP::new);
        functions.forEach(address -> {
            context.currentBuilder().MARK_LABEL(address.toString());
            context.randomFuzzFunction().accept(context);
            context.currentBuilder().PUSH_LABEL(Constants.PROGRAM_END_LABEL);
            context.currentBuilder().include(JUMP::new);
        });
        context.currentBuilder().MARK_LABEL(Constants.PROGRAM_END_LABEL);
    }),

    FUNCTION(1D, context -> {
        context.forRandom(1, Constants.LIMIT_INITIAL_CALL_DATA_LOAD, index -> {
            context.currentBuilder().include(() -> new PUSH(context.randomBetween(0, Constants.LIMIT_SIZE_CALL_DATA).toByteArray()));
            context.currentBuilder().include(() -> new LOAD(ProgramStoreZone.CALL_DATA));
        });
        context.randomFuzzFunction().accept(context);
    });

    private final Consumer<ProgramGeneratorContext> implementation;
    private final double chance;

    FuzzProgram(double chance, Consumer<ProgramGeneratorContext> implementation) {
        this.chance = chance;
        this.implementation = implementation;
    }

    @Override
    public double chance() {
        return this.chance;
    }

    @Override
    public void accept(ProgramGeneratorContext programGeneratorContext) {
        this.implementation.accept(programGeneratorContext);
    }

    interface Constants {
        int LIMIT_INITIAL_FUNCTIONS = 10;
        int LIMIT_INITIAL_CALL_DATA_LOAD = 10;
        int LIMIT_SIZE_CALL_DATA = 8192;
        String PROGRAM_END_LABEL = "end";
    }
}
