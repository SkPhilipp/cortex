package com.hileco.cortex.context;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.Instruction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Program {
    private List<Instruction> instructions;
    private LayeredMap<BigInteger, ProgramData> storage;

    public Program() {
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
}
