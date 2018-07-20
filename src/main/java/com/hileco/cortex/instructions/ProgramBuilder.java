package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProgramZone;

import java.util.ArrayList;
import java.util.List;

import static com.hileco.cortex.instructions.Operations.Add;
import static com.hileco.cortex.instructions.Operations.BitwiseAnd;
import static com.hileco.cortex.instructions.Operations.BitwiseNot;
import static com.hileco.cortex.instructions.Operations.BitwiseOr;
import static com.hileco.cortex.instructions.Operations.BitwiseXor;
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
        instructions.add(new Instruction<>(new Push(), data));
        return this;
    }

    public ProgramBuilder POP() {
        instructions.add(new Instruction<>(new Pop(), NO_DATA));
        return this;
    }

    public ProgramBuilder SWAP(int topOffsetLeft, int topOffsetRight) {
        Swap.Operands data = new Swap.Operands();
        data.topOffsetLeft = topOffsetLeft;
        data.topOffsetRight = topOffsetRight;
        instructions.add(new Instruction<>(new Swap(), data));
        return this;
    }

    public ProgramBuilder DUPLICATE(int topOffset) {
        Duplicate.Operands data = new Duplicate.Operands();
        data.topOffset = topOffset;
        instructions.add(new Instruction<>(new Duplicate(), data));
        return this;
    }

    public ProgramBuilder EQUALS() {
        instructions.add(new Instruction<>(new Equals(), NO_DATA));
        return this;
    }

    public ProgramBuilder GREATER_THAN() {
        instructions.add(new Instruction<>(new GreaterThan(), NO_DATA));
        return this;
    }

    public ProgramBuilder LESS_THAN() {
        instructions.add(new Instruction<>(new LessThan(), NO_DATA));
        return this;
    }

    public ProgramBuilder IS_ZERO() {
        instructions.add(new Instruction<>(new IsZero(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_OR() {
        instructions.add(new Instruction<>(new BitwiseOr(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_XOR() {
        instructions.add(new Instruction<>(new BitwiseXor(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_AND() {
        instructions.add(new Instruction<>(new BitwiseAnd(), NO_DATA));
        return this;
    }

    public ProgramBuilder BITWISE_NOT() {
        instructions.add(new Instruction<>(new BitwiseNot(), NO_DATA));
        return this;
    }

    public ProgramBuilder ADD() {
        instructions.add(new Instruction<>(new Add(), NO_DATA));
        return this;
    }

    public ProgramBuilder SUBTRACT() {
        instructions.add(new Instruction<>(new Subtract(), NO_DATA));
        return this;
    }

    public ProgramBuilder MULTIPLY() {
        instructions.add(new Instruction<>(new Multiply(), NO_DATA));
        return this;
    }

    public ProgramBuilder DIVIDE() {
        instructions.add(new Instruction<>(new Divide(), NO_DATA));
        return this;
    }

    public ProgramBuilder MODULO() {
        instructions.add(new Instruction<>(new Modulo(), NO_DATA));
        return this;
    }

    public ProgramBuilder HASH(String hashMethod) {
        Hash.Operands data = new Hash.Operands();
        data.hashMethod = hashMethod;
        instructions.add(new Instruction<>(new Hash(), data));
        return this;
    }

    public ProgramBuilder JUMP() {
        instructions.add(new Instruction<>(new Jump(), NO_DATA));
        return this;
    }

    public ProgramBuilder JUMP_DESTINATION() {
        instructions.add(new Instruction<>(new JumpDestination(), NO_DATA));
        return this;
    }

    public ProgramBuilder NOOP() {
        instructions.add(new Instruction<>(new NoOp(), NO_DATA));
        return this;
    }

    public ProgramBuilder JUMP_IF() {
        instructions.add(new Instruction<>(new JumpIf(), NO_DATA));
        return this;
    }

    public ProgramBuilder EXIT() {
        instructions.add(new Instruction<>(new Exit(), NO_DATA));
        return this;
    }

    public ProgramBuilder LOAD(ProgramZone programZone) {
        Load.Operands data = new Load.Operands();
        data.programZone = programZone;
        instructions.add(new Instruction<>(new Load(), data));
        return this;
    }

    public ProgramBuilder SAVE(ProgramZone programZone) {
        Save.Operands data = new Save.Operands();
        data.programZone = programZone;
        instructions.add(new Instruction<>(new Save(), data));
        return this;
    }

    public int currentSize() {
        return instructions.size();
    }
}
