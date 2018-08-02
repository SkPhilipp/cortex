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

    public void PUSH_LABEL(String name) {
        instructions.add(() -> {
            Push.Operands data = new Push.Operands();
            Integer address = labelAddresses.get(name);
            data.bytes = BigInteger.valueOf(address).toByteArray();
            return new Instruction<>(new Push(), data);
        });
    }

    public void PUSH(byte[] bytes) {
        Push.Operands data = new Push.Operands();
        data.bytes = bytes;
        instructions.add(() -> new Instruction<>(new Push(), data));
    }

    public void POP() {
        instructions.add(() -> new Instruction<>(new Pop(), NO_DATA));
    }

    public void SWAP(int topOffsetLeft, int topOffsetRight) {
        Swap.Operands data = new Swap.Operands();
        data.topOffsetLeft = topOffsetLeft;
        data.topOffsetRight = topOffsetRight;
        instructions.add(() -> new Instruction<>(new Swap(), data));
    }

    public void DUPLICATE(int topOffset) {
        Duplicate.Operands data = new Duplicate.Operands();
        data.topOffset = topOffset;
        instructions.add(() -> new Instruction<>(new Duplicate(), data));
    }

    public void EQUALS() {
        instructions.add(() -> new Instruction<>(new Equals(), NO_DATA));
    }

    public void GREATER_THAN() {
        instructions.add(() -> new Instruction<>(new GreaterThan(), NO_DATA));
    }

    public void LESS_THAN() {
        instructions.add(() -> new Instruction<>(new LessThan(), NO_DATA));
    }

    public void IS_ZERO() {
        instructions.add(() -> new Instruction<>(new IsZero(), NO_DATA));
    }

    public void BITWISE_OR() {
        instructions.add(() -> new Instruction<>(new BitwiseOr(), NO_DATA));
    }

    public void BITWISE_XOR() {
        instructions.add(() -> new Instruction<>(new BitwiseXor(), NO_DATA));
    }

    public void BITWISE_AND() {
        instructions.add(() -> new Instruction<>(new BitwiseAnd(), NO_DATA));
    }

    public void BITWISE_NOT() {
        instructions.add(() -> new Instruction<>(new BitwiseNot(), NO_DATA));
    }

    public void ADD() {
        instructions.add(() -> new Instruction<>(new Add(), NO_DATA));
    }

    public void SUBTRACT() {
        instructions.add(() -> new Instruction<>(new Subtract(), NO_DATA));
    }

    public void MULTIPLY() {
        instructions.add(() -> new Instruction<>(new Multiply(), NO_DATA));
    }

    public void DIVIDE() {
        instructions.add(() -> new Instruction<>(new Divide(), NO_DATA));
    }

    public void MODULO() {
        instructions.add(() -> new Instruction<>(new Modulo(), NO_DATA));
    }

    public void HASH(String hashMethod) {
        Hash.Operands data = new Hash.Operands();
        data.hashMethod = hashMethod;
        instructions.add(() -> new Instruction<>(new Hash(), data));
    }

    public void JUMP() {
        instructions.add(() -> new Instruction<>(new Jump(), NO_DATA));
    }

    public void JUMP_DESTINATION_WITH_LABEL(String name) {
        if (labelAddresses.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Name %s is already taken", name));
        }
        labelAddresses.put(name, instructions.size());
        instructions.add(() -> new Instruction<>(new JumpDestination(), NO_DATA));
    }

    public void JUMP_DESTINATION() {
        instructions.add(() -> new Instruction<>(new JumpDestination(), NO_DATA));
    }

    public void NOOP() {
        instructions.add(() -> new Instruction<>(new NoOp(), NO_DATA));
    }

    public void JUMP_IF() {
        instructions.add(() -> new Instruction<>(new JumpIf(), NO_DATA));
    }

    public void EXIT() {
        instructions.add(() -> new Instruction<>(new Exit(), NO_DATA));
    }

    public void LOAD(ProgramStoreZone programStoreZone) {
        Load.Operands data = new Load.Operands();
        data.programStoreZone = programStoreZone;
        instructions.add(() -> new Instruction<>(new Load(), data));
    }

    public void SAVE(ProgramStoreZone programStoreZone) {
        Save.Operands data = new Save.Operands();
        data.programStoreZone = programStoreZone;
        instructions.add(() -> new Instruction<>(new Save(), data));
    }

    public void CALL() {
        instructions.add(() -> new Instruction<>(new Call(), NO_DATA));
    }

    public void CALL_RETURN() {
        instructions.add(() -> new Instruction<>(new CallReturn(), NO_DATA));
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
