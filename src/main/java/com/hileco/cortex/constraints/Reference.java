package com.hileco.cortex.constraints;

import lombok.Value;

@Value
public class Reference {
    private Type type;
    private Long address;

    @Override
    public String toString() {
        return String.format("%s[%d]", this.type, this.address);
    }

    public enum Type {
        STACK,
        MEMORY,
        DISK,
        CALL_DATA
    }
}
