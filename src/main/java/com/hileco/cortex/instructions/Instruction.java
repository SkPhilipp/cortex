package com.hileco.cortex.instructions;

import lombok.Value;

import static com.hileco.cortex.instructions.Operations.Operation;

@Value
public class Instruction<T extends Operation<V>, V> {
    private final T operation;
    private final V operands;
}
