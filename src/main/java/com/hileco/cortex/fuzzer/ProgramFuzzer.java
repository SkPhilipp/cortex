package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramBuilder;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class ProgramFuzzer {

    private Random random;
    private ProgramBuilder programBuilder;
    private LayeredMap<Integer, Program> programAtlas;

    public ProgramFuzzer() {
        this.programBuilder = new ProgramBuilder();
        this.programAtlas = new LayeredMap<>();
        this.random = new Random();
    }

    public ProgramFuzzer writeExit() {
        programBuilder.EXIT();
        return this;
    }

    public ProgramFuzzer writeInfiniteLoop(Consumer<ProgramFuzzer> loop) {
        return writeLoop(fuzzer -> fuzzer.programBuilder.PUSH(BigInteger.valueOf(1).toByteArray()), loop);
    }

    public ProgramFuzzer writeLoop(Consumer<ProgramFuzzer> condition, Consumer<ProgramFuzzer> content) {
        final String startLabel = UUID.randomUUID().toString();
        final String endLabel = UUID.randomUUID().toString();
        programBuilder.JUMP_DESTINATION_WITH_LABEL(startLabel);
        condition.accept(this);
        programBuilder.IS_ZERO();
        programBuilder.PUSH_LABEL(endLabel);
        programBuilder.JUMP_IF();
        content.accept(this);
        programBuilder.PUSH_LABEL(startLabel);
        programBuilder.JUMP();
        programBuilder.JUMP_DESTINATION_WITH_LABEL(endLabel);
        return this;
    }

    public ProgramFuzzer inSwitch(Consumer<ProgramFuzzer> value, Map<byte[], Consumer<ProgramFuzzer>> cases) {
        // TODO: Implement
        // push key
        // for each case:
        //   if case key matches: jump to case value address
        // for each case:
        //   jump destination
        return this;
    }

    public ProgramFuzzer conditional(Consumer<ProgramFuzzer> condition, Consumer<ProgramFuzzer> content) {
        final String endLabel = UUID.randomUUID().toString();
        condition.accept(this);
        programBuilder.IS_ZERO();
        programBuilder.PUSH_LABEL(endLabel);
        programBuilder.JUMP_IF();
        content.accept(this);
        programBuilder.JUMP_DESTINATION_WITH_LABEL(endLabel);
        return this;
    }

    public ProgramFuzzer inCall(Consumer<ProgramFuzzer> block) {
        // TODO: Implement
        return this;
    }
}
