package com.hileco.cortex.instructions;

import static com.hileco.cortex.instructions.Instructions.*;

public class Instruction {
    final InstructionExecutor executor;
    final InstructionData data;

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
}
