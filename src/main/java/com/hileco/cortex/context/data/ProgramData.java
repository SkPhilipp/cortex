package com.hileco.cortex.context.data;

import java.util.HashSet;
import java.util.Set;

public class ProgramData {

    private byte[] content;
    private Set<ProgramDataSource> sources;

    public ProgramData(byte[] content, Set<ProgramDataSource> sources) {
        this.content = content;
        this.sources = sources;
    }

    public ProgramData(byte[] content) {
        super();
        this.content = content;
    }

    public ProgramData() {
        this.sources = new HashSet<>();
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Set<ProgramDataSource> getSources() {
        return sources;
    }

    public void setSources(Set<ProgramDataSource> sources) {
        this.sources = sources;
    }
}
