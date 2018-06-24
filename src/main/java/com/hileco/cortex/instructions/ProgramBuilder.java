package com.hileco.cortex.instructions;

import java.util.ArrayList;
import java.util.List;

import static com.hileco.cortex.instructions.Operations.*;

public class ProgramBuilder {
    private List<Instruction> instructions;

    ProgramBuilder() {
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> build() {
        return new ArrayList<>(instructions);
    }

    public ProgramBuilder PUSH(byte[] bytes) {
        Push.Operands data = new Push.Operands();
        data.bytes = bytes;
        instructions.add(new Instruction(new Push(), data));
        return this;
    }

    public ProgramBuilder POP() {
        instructions.add(new Instruction(new Pop(), NO_DATA));
        return this;
    }

    public ProgramBuilder SWAP(int topOffsetLeft, int topOffsetRight) {
        Swap.Operands data = new Swap.Operands();
        data.topOffsetLeft = topOffsetLeft;
        data.topOffsetRight = topOffsetRight;
        instructions.add(new Instruction(new Swap(), data));
        return this;
    }

    public ProgramBuilder DUPLICATE(int topOffset) {
        Duplicate.Operands data = new Duplicate.Operands();
        data.topOffset = topOffset;
        instructions.add(new Instruction(new Duplicate(), data));
        return this;
    }

    public ProgramBuilder EQUALS() {
        instructions.add(new Instruction(new Equals(), NO_DATA));
        return this;
    }

    public ProgramBuilder GREATER_THAN() {
        instructions.add(new Instruction(new GreaterThan(), NO_DATA));
        return this;
    }

    public ProgramBuilder LESS_THAN() {
        instructions.add(new Instruction(new LessThan(), NO_DATA));
        return this;
    }

    public ProgramBuilder IS_ZERO() {
        instructions.add(new Instruction(new IsZero(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_OR() {
        instructions.add(new Instruction(new BitwiseOr(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_XOR() {
        instructions.add(new Instruction(new BitwiseXor(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_AND() {
        instructions.add(new Instruction(new BitwiseAnd(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_NOT() {
        instructions.add(new Instruction(new BitwiseNot(), NO_DATA));
        return this;
    }

    public ProgramBuilder ADD() {
        instructions.add(new Instruction(new Add(), NO_DATA));
        return this;
    }

    public ProgramBuilder SUBTRACT() {
        instructions.add(new Instruction(new Subtract(), NO_DATA));
        return this;
    }

    public ProgramBuilder MULTIPLY() {
        instructions.add(new Instruction(new Multiply(), NO_DATA));
        return this;
    }

    public ProgramBuilder DIVIDE() {
        instructions.add(new Instruction(new Divide(), NO_DATA));
        return this;
    }

    public ProgramBuilder MODULO() {
        instructions.add(new Instruction(new Modulo(), NO_DATA));
        return this;
    }

    public ProgramBuilder HASH(String hashMethod) {
        Hash.Operands data = new Hash.Operands();
        data.hashMethod = hashMethod;
        instructions.add(new Instruction(new Hash(), data));
        return this;
    }

    public ProgramBuilder JUMP(int destination) {
        Jump.Operands data = new Jump.Operands();
        data.destination = destination;
        instructions.add(new Instruction(new Jump(), data));
        return this;
    }

    public ProgramBuilder JUMP_DESTINATION() {
        instructions.add(new Instruction(new JumpDestination(), NO_DATA));
        return this;
    }

    public ProgramBuilder NOOP() {
        instructions.add(new Instruction(new NoOp(), NO_DATA));
        return this;
    }

    public ProgramBuilder JUMP_IF(int destination) {
        JumpIf.Operands data = new JumpIf.Operands();
        data.destination = destination;
        instructions.add(new Instruction(new JumpIf(), data));
        return this;
    }

    public ProgramBuilder EXIT() {
        instructions.add(new Instruction(new Exit(), NO_DATA));
        return this;
    }

    public ProgramBuilder LOAD(String group, String address) {
        Load.Operands data = new Load.Operands();
        data.group = group;
        data.address = address;
        instructions.add(new Instruction(new Load(), data));
        return this;
    }

    public ProgramBuilder SAVE(String group, String address) {
        Save.Operands data = new Save.Operands();
        data.group = group;
        data.address = address;
        instructions.add(new Instruction(new Save(), data));
        return this;
    }
}
