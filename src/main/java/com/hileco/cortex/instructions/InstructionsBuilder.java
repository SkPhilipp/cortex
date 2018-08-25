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
        this.instructions = new ArrayList<>();
        this.labelAddresses = new HashMap<>();
    }

    public void include(Supplier<Instruction> supplier) {
        this.instructions.add(supplier);
    }

    public void PUSH_LABEL(String name) {
        this.include(() -> new PUSH(BigInteger.valueOf(this.labelAddresses.get(name)).toByteArray()));
    }

    public void MARK_LABEL(String name) {
        if (this.labelAddresses.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Name %s is already taken", name));
        }
        this.labelAddresses.put(name, this.instructions.size());
        this.include(JUMP_DESTINATION::new);
    }

    public void LOOP(Consumer<InstructionsBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        this.MARK_LABEL(startLabel);
        loopBody.accept(this);
        this.PUSH_LABEL(startLabel);
        this.include(JUMP::new);
    }

    public void LOOP(Consumer<InstructionsBuilder> conditionBody, Consumer<InstructionsBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        final String endLabel = UUID.randomUUID().toString();
        this.MARK_LABEL(startLabel);
        conditionBody.accept(this);
        this.include(IS_ZERO::new);
        this.PUSH_LABEL(endLabel);
        this.include(JUMP_IF::new);
        loopBody.accept(this);
        this.PUSH_LABEL(startLabel);
        this.include(JUMP::new);
        this.MARK_LABEL(endLabel);
    }

    public void IF(Consumer<InstructionsBuilder> condition, Consumer<InstructionsBuilder> content) {
        final String endLabel = UUID.randomUUID().toString();
        condition.accept(this);
        this.include(IS_ZERO::new);
        this.PUSH_LABEL(endLabel);
        this.include(JUMP_IF::new);
        content.accept(this);
        this.MARK_LABEL(endLabel);
    }

    public int size() {
        return this.instructions.size();
    }

    public List<Instruction> build() {
        return this.instructions.stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }

    public void include(List<Instruction> others) {
        others.forEach(instruction -> this.instructions.add(() -> instruction));
    }

}
