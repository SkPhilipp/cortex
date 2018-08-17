package com.hileco.cortex.instructions;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Operations.Call;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.hileco.cortex.instructions.Operations.Add;
import static com.hileco.cortex.instructions.Operations.BitwiseAnd;
import static com.hileco.cortex.instructions.Operations.BitwiseNot;
import static com.hileco.cortex.instructions.Operations.BitwiseOr;
import static com.hileco.cortex.instructions.Operations.BitwiseXor;
import static com.hileco.cortex.instructions.Operations.CallReturn;
import static com.hileco.cortex.instructions.Operations.Divide;
import static com.hileco.cortex.instructions.Operations.Duplicate;
import static com.hileco.cortex.instructions.Operations.Equals;
import static com.hileco.cortex.instructions.Operations.Exit;
import static com.hileco.cortex.instructions.Operations.GreaterThan;
import static com.hileco.cortex.instructions.Operations.Hash;
import static com.hileco.cortex.instructions.Operations.IsZero;
import static com.hileco.cortex.instructions.Operations.Jump;
import static com.hileco.cortex.instructions.Operations.JumpDestination;
import static com.hileco.cortex.instructions.Operations.JumpIf;
import static com.hileco.cortex.instructions.Operations.LessThan;
import static com.hileco.cortex.instructions.Operations.Load;
import static com.hileco.cortex.instructions.Operations.Modulo;
import static com.hileco.cortex.instructions.Operations.Multiply;
import static com.hileco.cortex.instructions.Operations.NO_DATA;
import static com.hileco.cortex.instructions.Operations.NoOp;
import static com.hileco.cortex.instructions.Operations.Pop;
import static com.hileco.cortex.instructions.Operations.Push;
import static com.hileco.cortex.instructions.Operations.Save;
import static com.hileco.cortex.instructions.Operations.Subtract;
import static com.hileco.cortex.instructions.Operations.Swap;

public class ProgramBuilder {
    private List<Supplier<Instruction>> instructions;
    private Map<String, Integer> labelAddresses;

    public ProgramBuilder() {
        instructions = new ArrayList<>();
        labelAddresses = new HashMap<>();
    }

    public Supplier<Instruction> PUSH_LABEL(String name) {
        Supplier<Instruction> supplier = () -> {
            Push.Operands data = new Push.Operands();
            Integer address = labelAddresses.get(name);
            data.bytes = BigInteger.valueOf(address).toByteArray();
            return new Instruction<>(new Push(), data);
        };
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> PUSH(byte[] bytes) {
        Push.Operands data = new Push.Operands();
        data.bytes = bytes;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Push(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> POP() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Pop(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> SWAP(int topOffsetLeft, int topOffsetRight) {
        Swap.Operands data = new Swap.Operands();
        data.topOffsetLeft = topOffsetLeft;
        data.topOffsetRight = topOffsetRight;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Swap(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> DUPLICATE(int topOffset) {
        Duplicate.Operands data = new Duplicate.Operands();
        data.topOffset = topOffset;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Duplicate(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> EQUALS() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Equals(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> GREATER_THAN() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new GreaterThan(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> LESS_THAN() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new LessThan(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> IS_ZERO() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new IsZero(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> BITWISE_OR() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new BitwiseOr(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> BITWISE_XOR() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new BitwiseXor(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> BITWISE_AND() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new BitwiseAnd(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> BITWISE_NOT() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new BitwiseNot(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> ADD() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Add(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> SUBTRACT() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Subtract(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> MULTIPLY() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Multiply(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> DIVIDE() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Divide(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> MODULO() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Modulo(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> HASH(String hashMethod) {
        Hash.Operands data = new Hash.Operands();
        data.hashMethod = hashMethod;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Hash(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> JUMP() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Jump(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> JUMP_DESTINATION_WITH_LABEL(String name) {
        if (labelAddresses.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Name %s is already taken", name));
        }
        labelAddresses.put(name, instructions.size());
        Supplier<Instruction> supplier = () -> new Instruction<>(new JumpDestination(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> JUMP_DESTINATION() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new JumpDestination(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> NOOP() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new NoOp(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> JUMP_IF() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new JumpIf(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> EXIT() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Exit(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> LOAD(ProgramStoreZone programStoreZone) {
        Load.Operands data = new Load.Operands();
        data.programStoreZone = programStoreZone;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Load(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> SAVE(ProgramStoreZone programStoreZone) {
        Save.Operands data = new Save.Operands();
        data.programStoreZone = programStoreZone;
        Supplier<Instruction> supplier = () -> new Instruction<>(new Save(), data);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> CALL() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new Call(), NO_DATA);
        instructions.add(supplier);
        return supplier;
    }

    public Supplier<Instruction> CALL_RETURN() {
        Supplier<Instruction> supplier = () -> new Instruction<>(new CallReturn(), NO_DATA);
        instructions.add(supplier);
        return supplier;
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
        return build(null);
    }

    public Program build(BigInteger address) {
        List<Instruction> instructions = this.instructions.stream().map(Supplier::get).collect(Collectors.toList());
        return new Program(address, new ArrayList<>(instructions));
    }

    public void include(List<Instruction> others) {
        others.forEach(instruction -> instructions.add(() -> instruction));
    }

}
