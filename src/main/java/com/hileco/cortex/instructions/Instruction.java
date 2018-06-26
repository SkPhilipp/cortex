package com.hileco.cortex.instructions;

import static com.hileco.cortex.instructions.Operations.Operands;
import static com.hileco.cortex.instructions.Operations.Operation;

public class Instruction<T extends Operation<V>, V extends Operands> {
    private final T operation;
    private final V operands;

    Instruction(T operation, V operands) {
        this.operation = operation;
        this.operands = operands;
    }

    public T getOperation() {
        return operation;
    }

    public V getOperands() {
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
