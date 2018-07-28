package com.hileco.cortex.context;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;

public class ProgramContext {

    private int instructionsExecuted;
    private int instructionLimit;
    private int instructionPosition;
    private LayeredStack<byte[]> stack;
    private LayeredMap<BigInteger, ProgramData> memoryStorage;
    private Program program;
    private BigInteger returnDataOffset;
    private BigInteger returnDataSize;
    private ProgramData callData;

    public ProgramContext(Program program) {
        this.instructionsExecuted = 0;
        this.instructionPosition = 0;
        this.instructionLimit = 1000000;
        this.stack = new LayeredStack<>();
        this.memoryStorage = new LayeredMap<>();
        this.program = program;
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

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public BigInteger getReturnDataOffset() {
        return returnDataOffset;
    }

    public void setReturnDataOffset(BigInteger returnDataOffset) {
        this.returnDataOffset = returnDataOffset;
    }

    public BigInteger getReturnDataSize() {
        return returnDataSize;
    }

    public void setReturnDataSize(BigInteger returnDataSize) {
        this.returnDataSize = returnDataSize;
    }

    public ProgramData getCallData() {
        return callData;
    }

    public void setCallData(ProgramData callData) {
        this.callData = callData;
    }

    @Override
    public String toString() {
        return "ProgramContext{" +
                "instructionsExecuted=" + instructionsExecuted +
                ", instructionLimit=" + instructionLimit +
                ", instructionPosition=" + instructionPosition +
                ", stack=" + stack +
                ", memoryStorage=" + memoryStorage +
                ", program=" + program +
                ", returnDataOffset=" + returnDataOffset +
                ", returnDataSize=" + returnDataSize +
                ", callData=" + callData +
                '}';
    }
}