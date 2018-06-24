package com.hileco.cortex.instructions;

import static com.hileco.cortex.instructions.Operations.*;

public class Instruction {
    private final Operation operation;
    private final Operands operands;

    Instruction(Operation operation, Operands operands) {
        this.operation = operation;
        this.operands = operands;
    }

    public Operation getOperation() {
        return operation;
    }

    public Operands getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "operation=" + operation +
                ", operands=" + operands +
                '}';
    }
}
