package com.hileco.cortex.instructions;

import lombok.Value;

@Value
public class StackParameter {
    private final String name;
    private final Integer position;
}
