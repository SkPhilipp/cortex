package com.hileco.cortex.instructions;

import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InstructionsBuilder {
    private List<Supplier<Instruction>> instructions;
    private Map<String, Integer> labelAddresses;

    public InstructionsBuilder() {
        instructions = new ArrayList<>();
        labelAddresses = new HashMap<>();
    }

    public void include(Supplier<Instruction> supplier) {
        instructions.add(supplier);
    }

    public void PUSH_LABEL(String name) {
        include(() -> new PUSH(BigInteger.valueOf(labelAddresses.get(name)).toByteArray()));
    }

    public void MARK_LABEL(String name) {
        if (labelAddresses.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Name %s is already taken", name));
        }
        labelAddresses.put(name, instructions.size());
        include(JUMP_DESTINATION::new);
    }

    public void LOOP(Consumer<InstructionsBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        MARK_LABEL(startLabel);
        loopBody.accept(this);
        PUSH_LABEL(startLabel);
        include(JUMP::new);
    }

    public void LOOP(Consumer<InstructionsBuilder> conditionBody, Consumer<InstructionsBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        final String endLabel = UUID.randomUUID().toString();
        MARK_LABEL(startLabel);
        conditionBody.accept(this);
        include(IS_ZERO::new);
        PUSH_LABEL(endLabel);
        include(JUMP_IF::new);
        loopBody.accept(this);
        PUSH_LABEL(startLabel);
        include(JUMP::new);
        MARK_LABEL(endLabel);
    }

    public void IF(Consumer<InstructionsBuilder> condition, Consumer<InstructionsBuilder> content) {
        final String endLabel = UUID.randomUUID().toString();
        condition.accept(this);
        include(IS_ZERO::new);
        PUSH_LABEL(endLabel);
        include(JUMP_IF::new);
        content.accept(this);
        MARK_LABEL(endLabel);
    }

    public int size() {
        return instructions.size();
    }

    public List<Instruction> build() {
        return instructions.stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }

    public void include(List<Instruction> others) {
        others.forEach(instruction -> instructions.add(() -> instruction));
    }

}
