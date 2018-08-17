package com.hileco.cortex.instructions;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_NOT;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.bits.BITWISE_XOR;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.calls.CALL_RETURN;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.io.SAVE;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.math.HASH;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ProgramBuilder {
    private List<Supplier<Instruction>> instructions;
    private Map<String, Integer> labelAddresses;

    public ProgramBuilder() {
        instructions = new ArrayList<>();
        labelAddresses = new HashMap<>();
    }

    private Supplier<Instruction> register(Supplier<Instruction> supplier) {
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> PUSH_LABEL(String name) {
        return register(() -> new PUSH(BigInteger.valueOf(labelAddresses.get(name)).toByteArray()));
    }

    public Supplier<Instruction> PUSH(byte[] bytes) {
        return register(() -> new PUSH(bytes));
    }

    public Supplier<Instruction> POP() {
        return register(POP::new);
    }

    public Supplier<Instruction> SWAP(int topOffsetLeft, int topOffsetRight) {
        return register(() -> new SWAP(topOffsetLeft, topOffsetRight));
    }

    public Supplier<Instruction> DUPLICATE(int topOffset) {
        return register(() -> new DUPLICATE(topOffset));
    }

    public Supplier<Instruction> EQUALS() {
        return register(EQUALS::new);
    }

    public Supplier<Instruction> GREATER_THAN() {
        return register(GREATER_THAN::new);
    }

    public Supplier<Instruction> LESS_THAN() {
        return register(LESS_THAN::new);
    }

    public Supplier<Instruction> IS_ZERO() {
        return register(IS_ZERO::new);
    }

    public Supplier<Instruction> BITWISE_OR() {
        return register(BITWISE_OR::new);
    }

    public Supplier<Instruction> BITWISE_XOR() {
        return register(BITWISE_XOR::new);
    }

    public Supplier<Instruction> BITWISE_AND() {
        return register(BITWISE_AND::new);
    }

    public Supplier<Instruction> BITWISE_NOT() {
        return register(BITWISE_NOT::new);
    }

    public Supplier<Instruction> ADD() {
        return register(ADD::new);
    }

    public Supplier<Instruction> SUBTRACT() {
        return register(SUBTRACT::new);
    }

    public Supplier<Instruction> MULTIPLY() {
        return register(MULTIPLY::new);
    }

    public Supplier<Instruction> DIVIDE() {
        return register(DIVIDE::new);
    }

    public Supplier<Instruction> MODULO() {
        return register(MODULO::new);
    }

    public Supplier<Instruction> HASH(String hashMethod) {
        return register(() -> new HASH(hashMethod));
    }

    public Supplier<Instruction> JUMP() {
        return register(JUMP::new);
    }

    public Supplier<Instruction> JUMP_DESTINATION_WITH_LABEL(String name) {
        if (labelAddresses.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Name %s is already taken", name));
        }
        labelAddresses.put(name, instructions.size());
        return register(JUMP_DESTINATION::new);
    }

    public Supplier<Instruction> JUMP_DESTINATION() {
        return register(JUMP_DESTINATION::new);
    }

    public Supplier<Instruction> NOOP() {
        return register(NOOP::new);
    }

    public Supplier<Instruction> JUMP_IF() {
        return register(JUMP_IF::new);
    }

    public Supplier<Instruction> EXIT() {
        return register(EXIT::new);
    }

    public Supplier<Instruction> LOAD(ProgramStoreZone programStoreZone) {
        return register(() -> new LOAD(programStoreZone));
    }

    public Supplier<Instruction> SAVE(ProgramStoreZone programStoreZone) {
        return register(() -> new SAVE(programStoreZone));
    }

    public Supplier<Instruction> CALL() {
        return register(CALL::new);
    }

    public Supplier<Instruction> CALL_RETURN() {
        return register(CALL_RETURN::new);
    }

    public void CONSTRUCT_INFINITE_LOOP(Consumer<ProgramBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        JUMP_DESTINATION_WITH_LABEL(startLabel);
        loopBody.accept(this);
        PUSH_LABEL(startLabel);
        JUMP();
    }

    public void CONSTRUCT_LOOP(Consumer<ProgramBuilder> conditionBody, Consumer<ProgramBuilder> loopBody) {
        final String startLabel = UUID.randomUUID().toString();
        final String endLabel = UUID.randomUUID().toString();
        JUMP_DESTINATION_WITH_LABEL(startLabel);
        conditionBody.accept(this);
        IS_ZERO();
        PUSH_LABEL(endLabel);
        JUMP_IF();
        loopBody.accept(this);
        PUSH_LABEL(startLabel);
        JUMP();
        JUMP_DESTINATION_WITH_LABEL(endLabel);
    }

    public void CONSTRUCT_IF(Consumer<ProgramBuilder> condition, Consumer<ProgramBuilder> content) {
        final String endLabel = UUID.randomUUID().toString();
        condition.accept(this);
        IS_ZERO();
        PUSH_LABEL(endLabel);
        JUMP_IF();
        content.accept(this);
        JUMP_DESTINATION_WITH_LABEL(endLabel);
    }

    public int currentSize() {
        return instructions.size();
    }

    public void include(ProgramBuilder programBuilder) {
        int currentSize = instructions.size();
        programBuilder.labelAddresses.forEach((name, address) -> {
            if (labelAddresses.containsKey(name)) {
                throw new IllegalArgumentException(String.format("Name %s is already taken", name));
            }
            labelAddresses.put(name, address + currentSize);
        });
        instructions.addAll(programBuilder.instructions);
    }

    public Program build() {
        return build(BigInteger.ZERO);
    }

    public Program build(BigInteger address) {
        List<Instruction> instructions = this.instructions.stream().map(Supplier::get).collect(Collectors.toList());
        return new Program(address, new ArrayList<>(instructions));
    }

    public void include(List<Instruction> others) {
        others.forEach(instruction -> instructions.add(() -> instruction));
    }

}
