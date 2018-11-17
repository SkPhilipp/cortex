package com.hileco.cortex.vm.data;

import lombok.Value;

import java.util.Set;

@Value
public class ProgramData {
    private final byte[] content;
    private final Set<ProgramDataSource> sources;
}
