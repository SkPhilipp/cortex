package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.data.ProgramStoreZone;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.Consumer;

public enum FuzzFunction implements Chanced, Consumer<ProgramGeneratorContext> {

    EXIT(0.3D, context -> {
        context.builder().EXIT();
    }),

    RETURN(3D, context -> {
        context.builder().CALL_RETURN();
    }),

    CALL_WITH_FUNDS(1D, context -> {
        context.builder().PUSH(context.random().toByteArray());
        context.builder().PUSH(context.random().toByteArray());
        context.builder().SWAP(0, context.randomIntBetween(1, Constants.STACK_SWAP_UPPER_BOUND));
        context.builder().CALL();
    }),

    CALL_LIBRARY(1D, context -> {
        Set<BigInteger> choices = context.atlas().keySet();
        if (!choices.isEmpty()) {
            BigInteger address = (BigInteger) choices.toArray()[context.randomIntBetween(0, choices.size())];
            context.builder().PUSH(BigInteger.valueOf(0).toByteArray());
            context.builder().PUSH(address.toByteArray());
            context.builder().CALL();
        } else {
            context.builder().EXIT();
        }
    }),

    SAVE(3D, context -> {
        context.builder().SAVE(ProgramStoreZone.DISK);
    });

    interface Constants {
        int STACK_SWAP_UPPER_BOUND = 10;
    }

    private double chance;
    private final Consumer<ProgramGeneratorContext> implementation;

    FuzzFunction(double chance, Consumer<ProgramGeneratorContext> implementation) {
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
