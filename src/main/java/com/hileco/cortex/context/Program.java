package com.hileco.cortex.context;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Program {
    private List<Instruction> instructions;
    private LayeredMap<BigInteger, ProgramData> storage;
    private LayeredStack<Pair<BigInteger, BigInteger>> transfers;
    private BigInteger address;

    public Program() {
        this.address = null;
        this.transfers = new LayeredStack<>();
        this.instructions = new ArrayList<>();
        this.storage = new LayeredMap<>();
    }

    public Program(BigInteger address) {
        this.address = address;
        this.transfers = new LayeredStack<>();
        this.instructions = new ArrayList<>();
        this.storage = new LayeredMap<>();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public LayeredMap<BigInteger, ProgramData> getStorage() {
        return storage;
    }

    public void setStorage(LayeredMap<BigInteger, ProgramData> storage) {
        this.storage = storage;
    }

    public LayeredStack<Pair<BigInteger, BigInteger>> getTransfers() {
        return transfers;
    }

    public void setTransfers(LayeredStack<Pair<BigInteger, BigInteger>> transfers) {
        this.transfers = transfers;
    }

    public BigInteger getAddress() {
        if (address == null) {
            throw new IllegalStateException("This program does not have an address.");
        }
        return address;
    }

    public void setAddress(BigInteger address) {
        this.address = address;
    }
}
