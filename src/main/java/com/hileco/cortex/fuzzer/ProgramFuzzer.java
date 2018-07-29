package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProgramFuzzer {

    // TODO: Incorporate save-load / hashmap logic

    private ProgramBuilder programBuilder;
    private LayeredMap<Integer, Program> programAtlas;

    public ProgramFuzzer() {
        this.programBuilder = new ProgramBuilder();
        this.programAtlas = new LayeredMap<>();
    }

    public ProgramFuzzer writeExit() {
        programBuilder.EXIT();
        return this;
    }

    public ProgramFuzzer writeInfiniteLoop(Consumer<ProgramFuzzer> loop) {
        return writeLoop(fuzzer -> fuzzer.programBuilder.PUSH(BigInteger.valueOf(1).toByteArray()), loop);
    }

    public ProgramFuzzer writeLoop(Consumer<ProgramFuzzer> condition, Consumer<ProgramFuzzer> content) {
        List<Instruction> conditionInstructions = toInstructions(condition);
        List<Instruction> contentInstructions = toInstructions(content);
        int startAddress = programBuilder.currentSize();
        int loopInternals = 7;
        int endAddress = startAddress + conditionInstructions.size() + contentInstructions.size() + loopInternals;
        programBuilder.JUMP_DESTINATION();
        programBuilder.include(conditionInstructions);
        programBuilder.IS_ZERO();
        programBuilder.PUSH(BigInteger.valueOf(endAddress).toByteArray());
        programBuilder.JUMP_IF();
        programBuilder.include(contentInstructions);
        programBuilder.PUSH(BigInteger.valueOf(startAddress).toByteArray());
        programBuilder.JUMP();
        programBuilder.JUMP_DESTINATION();
        return this;
    }

    public ProgramFuzzer writeFunctionTable(Consumer<ProgramFuzzer>... functions) {
        // TODO: Create a map out of the functions list inSwitch
        // TODO: Load from call data (?)
        // TODO: Implement Memory and Storage as a map on a virtual byte[]
        return this;
    }

    public ProgramFuzzer inSwitch(Consumer<ProgramFuzzer> value, Pair<BigInteger, Consumer<ProgramFuzzer>>... cases) {
        List<Pair<BigInteger, List<Instruction>>> entries = Arrays.stream(cases)
                .map(pair -> pair.mapValue(this::toInstructions))
                .collect(Collectors.toList());
        // TODO: Write unique JUMP_IF's per item on the switch
        return this;
    }

    public ProgramFuzzer conditional(Consumer<ProgramFuzzer> condition, Consumer<ProgramFuzzer> content) {
        return this;
    }

    public ProgramFuzzer inCall(Consumer<ProgramFuzzer> block) {
        toInstructions(block);
        return this;
    }

    private List<Instruction> toInstructions(Consumer<ProgramFuzzer> consumer) {
        ProgramFuzzer subFuzzer = new ProgramFuzzer();
        consumer.accept(subFuzzer);
        subFuzzer.programAtlas.keySet().forEach(registration ->
                programAtlas.put(registration, subFuzzer.programAtlas.get(registration)));
        return subFuzzer.programBuilder.build().getInstructions();
    }
}
