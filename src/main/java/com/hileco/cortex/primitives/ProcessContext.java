package com.hileco.cortex.primitives;

import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

    private boolean jumpDestinaitonRequired;
    private boolean exiting;
    private long counter;
    private LayeredStack<byte[]> stack;
    private Map<String, LayeredMap<String, byte[]>> storage;

    public ProcessContext() {
        this.jumpDestinaitonRequired = false;
        this.exiting = false;
        this.counter = 0;
        this.stack = new LayeredStack<>();
        this.storage = new HashMap<>();
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public LayeredStack<byte[]> getStack() {
        return stack;
    }

    public void setStack(LayeredStack<byte[]> stack) {
        this.stack = stack;
    }

    public Map<String, LayeredMap<String, byte[]>> getStorage() {
        return storage;
    }

    public void setStorage(Map<String, LayeredMap<String, byte[]>> storage) {
        this.storage = storage;
    }

    public boolean isJumpDestinaitonRequired() {
        return jumpDestinaitonRequired;
    }

    public void setJumpDestinaitonRequired(boolean jumpDestinaitonRequired) {
        this.jumpDestinaitonRequired = jumpDestinaitonRequired;
    }

    public boolean isExiting() {
        return exiting;
    }

    public void setExiting(boolean exiting) {
        this.exiting = exiting;
    }
}
