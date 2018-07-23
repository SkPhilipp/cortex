package com.hileco.cortex.context;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;

public class ProgramContext {

    public enum ProgramState {
        DEFAULT, IN_JUMP, IN_CALL, IN_EXIT
    }

    private ProgramState state;
    private int instructionsExecuted;
    private int instructionLimit;
    private int instructionPosition;
    private LayeredStack<byte[]> stack;
    private LayeredMap<BigInteger, ProgramData> memoryStorage;
    private LayeredMap<BigInteger, ProgramData> diskStorage;
    private LayeredMap<BigInteger, ProgramData> callDataStorage;
    private Program program;

    public ProgramContext(Program program) {
        this.instructionsExecuted = 0;
        this.instructionPosition = 0;
        this.instructionLimit = 1000000;
        this.stack = new LayeredStack<>();
        this.memoryStorage = new LayeredMap<>();
        this.diskStorage = new LayeredMap<>();
        this.callDataStorage = new LayeredMap<>();
        this.program = program;
    }

    public ProgramState getState() {
        return state;
    }

    public void setState(ProgramState state) {
        this.state = state;
    }

    public int getInstructionsExecuted() {
        return instructionsExecuted;
    }

    public void setInstructionsExecuted(int instructionsExecuted) {
        this.instructionsExecuted = instructionsExecuted;
    }

    public int getInstructionLimit() {
        return instructionLimit;
    }

    public void setInstructionLimit(int instructionLimit) {
        this.instructionLimit = instructionLimit;
    }

    public int getInstructionPosition() {
        return instructionPosition;
    }

    public void setInstructionPosition(int instructionPosition) {
        this.instructionPosition = instructionPosition;
    }

    public LayeredStack<byte[]> getStack() {
        return stack;
    }

    public void setStack(LayeredStack<byte[]> stack) {
        this.stack = stack;
    }

    public LayeredMap<BigInteger, ProgramData> getMemoryStorage() {
        return memoryStorage;
    }

    public void setMemoryStorage(LayeredMap<BigInteger, ProgramData> memoryStorage) {
        this.memoryStorage = memoryStorage;
    }

    public LayeredMap<BigInteger, ProgramData> getDiskStorage() {
        return diskStorage;
    }

    public void setDiskStorage(LayeredMap<BigInteger, ProgramData> diskStorage) {
        this.diskStorage = diskStorage;
    }

    public LayeredMap<BigInteger, ProgramData> getCallDataStorage() {
        return callDataStorage;
    }

    public void setCallDataStorage(LayeredMap<BigInteger, ProgramData> callDataStorage) {
        this.callDataStorage = callDataStorage;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "ProgramContext{" +
                "state=" + state +
                ", instructionsExecuted=" + instructionsExecuted +
                ", instructionLimit=" + instructionLimit +
                ", instructionPosition=" + instructionPosition +
                ", stack=" + stack +
                ", memoryStorage=" + memoryStorage +
                ", diskStorage=" + diskStorage +
                ", callDataStorage=" + callDataStorage +
                ", program=" + program +
                '}';
    }
}