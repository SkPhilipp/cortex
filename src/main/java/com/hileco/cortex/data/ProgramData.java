package com.hileco.cortex.data;

import java.util.Arrays;

public class ProgramData {

    public ProgramData(byte[] content) {
        this.content = content;
        this.scope = ProgramDataScope.CHANGING;
    }

    public ProgramData(byte[] content, ProgramDataScope scope) {
        this.content = content;
        this.scope = scope;
    }

    public byte[] content;
    public ProgramDataScope scope;

    @Override
    public String toString() {
        return "ProgramData{" +
                "content=" + Arrays.toString(content) +
                ", scope=" + scope +
                '}';
    }
}
