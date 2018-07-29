package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;

import java.math.BigInteger;
import java.util.List;

public class Program {
    private final List<Instruction> instructions;
    private final LayeredBytes storage;
    private final LayeredStack<Pair<BigInteger, BigInteger>> transfers;
    private final BigInteger address;

    public Program(List<Instruction> instructions) {
        this.address = null;
        this.transfers = new LayeredStack<>();
        this.instructions = instructions;
        this.storage = new LayeredBytes();
    }

    public Program(BigInteger address, List<Instruction> instructions) {
        this.address = address;
        this.transfers = new LayeredStack<>();
        this.instructions = instructions;
        this.storage = new LayeredBytes();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public LayeredBytes getStorage() {
        return storage;
    }

    public LayeredStack<Pair<BigInteger, BigInteger>> getTransfers() {
        return transfers;
    }

    public BigInteger getAddress() {
        if (address == null) {
            throw new IllegalStateException("This program does not have an address.");
        }
        return address;
    }
}
