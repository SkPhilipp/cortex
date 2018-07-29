package com.hileco.cortex.context;

import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;

public class ProgramContext {

    private int instructionsExecuted;
    private int instructionLimit;
    private int instructionPosition;
    private LayeredStack<byte[]> stack;
    private LayeredBytes memory;
    private Program program;
    private BigInteger returnDataOffset;
    private BigInteger returnDataSize;
    private LayeredBytes callData;

    public ProgramContext(Program program) {
        this.instructionsExecuted = 0;
        this.instructionPosition = 0;
        this.instructionLimit = 1000000;
        this.stack = new LayeredStack<>();
        this.memory = new LayeredBytes();
        this.callData = new LayeredBytes();
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

    public LayeredBytes getMemory() {
        return memory;
    }

    public void setMemory(LayeredBytes memory) {
        this.memory = memory;
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

    public LayeredBytes getCallData() {
        return callData;
    }

    @Override
    public String toString() {
        return "ProgramContext{" +
                "instructionsExecuted=" + instructionsExecuted +
                ", instructionLimit=" + instructionLimit +
                ", instructionPosition=" + instructionPosition +
                ", stack=" + stack +
                ", memory=" + memory +
                ", program=" + program +
                ", returnDataOffset=" + returnDataOffset +
                ", returnDataSize=" + returnDataSize +
                ", callData=" + callData +
                '}';
    }
}