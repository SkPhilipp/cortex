package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.math.BigInteger;
import java.util.List;

@Value
public class Program {
    private final List<Instruction> instructions;
    private final LayeredBytes storage;
    private final LayeredStack<Pair<BigInteger, BigInteger>> transfers;
    private final BigInteger address;

    public Program(List<Instruction> instructions) {
        this(BigInteger.ZERO, instructions);
    }

    public Program(BigInteger address, List<Instruction> instructions) {
        this.address = address;
        this.transfers = new LayeredStack<>();
        this.instructions = instructions;
        this.storage = new LayeredBytes();
    }

    public BigInteger getAddress() {
        if (this.address == null) {
            throw new IllegalStateException("This program does not have an address.");
        }
        return this.address;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        var size = this.instructions.size();
        for (var i = 0; i < size; i++) {
            stringBuilder.append(String.format(" %06d │ %s\n", i, this.instructions.get(i)));
        }
        stringBuilder.append("        └───────────────────────────────────\n");
        return stringBuilder.toString();
    }
}
