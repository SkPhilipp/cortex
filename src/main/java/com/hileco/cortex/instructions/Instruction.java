package com.hileco.cortex.instructions;

import static com.hileco.cortex.instructions.Instructions.*;

public class Instruction {
    private final InstructionExecutor executor;
    private final InstructionData data;

    Instruction(InstructionExecutor executor, InstructionData data) {
        this.executor = executor;
        this.data = data;
    }

    public InstructionExecutor getExecutor() {
        return executor;
    }

    public InstructionData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "executor=" + executor +
                ", data=" + data +
                '}';
    }
}
