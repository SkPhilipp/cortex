package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.calls.CALL_RETURN;
import com.hileco.cortex.instructions.io.SAVE;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;

import java.math.BigInteger;
import java.util.function.Consumer;

public enum FuzzFunction implements Chanced, Consumer<ProgramGeneratorContext> {

    EXIT_ONLY(0.3D, context -> {
        context.currentBuilder().include(EXIT::new);
    }),

    RETURN_ONLY(3D, context -> {
        context.currentBuilder().include(CALL_RETURN::new);
    }),

    CALL_WITH_FUNDS(1D, context -> {
        context.currentBuilder().include(() -> new PUSH(context.random().toByteArray()));
        context.currentBuilder().include(() -> new PUSH(context.random().toByteArray()));
        context.currentBuilder().include(() -> new SWAP(0, context.randomIntBetween(1, Constants.STACK_SWAP_UPPER_BOUND)));
        context.currentBuilder().include(CALL::new);
    }),

    CALL_LIBRARY(1D, context -> {
        var choices = context.atlas().keySet();
        if (!choices.isEmpty()) {
            var address = (BigInteger) choices.toArray()[context.randomIntBetween(0, choices.size())];
            context.currentBuilder().include(() -> new PUSH(BigInteger.ZERO.toByteArray()));
            context.currentBuilder().include(() -> new PUSH(address.toByteArray()));
            context.currentBuilder().include(CALL::new);
        } else {
            context.currentBuilder().include(EXIT::new);
        }
    }),

    SAVE_ONLY(3D, context -> {
        context.currentBuilder().include(() -> new SAVE(ProgramStoreZone.DISK));
    });

    private final Consumer<ProgramGeneratorContext> implementation;
    private final double chance;
    FuzzFunction(double chance, Consumer<ProgramGeneratorContext> implementation) {
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
        int STACK_SWAP_UPPER_BOUND = 10;
    }
}
