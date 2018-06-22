package com.hileco.cortex.primitives;

import java.util.Map;

public class VirtualMachineContext {
    private LayeredStack<ProcessContext> process;
    private Map<String, LayeredMap<String, byte[]>> storage;
}
