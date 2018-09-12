package com.hileco.cortex.constraints;

import lombok.Value;

@Value
public class Reference {
    public enum Type {
        STACK,
        MEMORY,
        DISK,
        CALL_DATA
    }

    private Type type;
    private Long address;
}
