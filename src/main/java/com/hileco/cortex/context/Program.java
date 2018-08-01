package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import javafx.util.Pair;
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
        this(null, instructions);
    }

    public Program(BigInteger address, List<Instruction> instructions) {
        this.address = address;
        transfers = new LayeredStack<>();
        this.instructions = instructions;
        storage = new LayeredBytes();
    }

    public BigInteger getAddress() {
        if (address == null) {
            throw new IllegalStateException("This program does not have an address.");
        }
        return address;
    }
}
