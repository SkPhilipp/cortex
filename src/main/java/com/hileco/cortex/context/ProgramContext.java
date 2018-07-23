package com.hileco.cortex.context;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ProgramContext {

    private boolean jumping;
    private boolean exiting;
    private int instructionsExecuted;
    private int instructionLimit;
    private int instructionPosition;
    private LayeredStack<byte[]> stack;
    private int stackLimit;
    private Map<ProgramZone, LayeredMap<BigInteger, ProgramData>> storage;
    private BigInteger overflowLimit;
    private BigInteger underflowLimit;

    public ProgramContext() {
        this.jumping = false;
        this.exiting = false;
        this.instructionsExecuted = 0;
        this.instructionPosition = 0;
        this.instructionLimit = 1000000;
        this.stack = new LayeredStack<>();
        this.stackLimit = 1024;
        this.storage = new HashMap<>();
        this.overflowLimit = new BigInteger(new byte[]{2}).pow(256).subtract(BigInteger.ONE);
        this.underflowLimit = new BigInteger(new byte[]{-2}).pow(256).subtract(BigInteger.ONE);
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isExiting() {
        return exiting;
    }

    public void setExiting(boolean exiting) {
        this.exiting = exiting;
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

    public int getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(int stackLimit) {
        this.stackLimit = stackLimit;
    }

    public ProgramData getData(ProgramZone programZone, BigInteger address) {
        return storage.computeIfAbsent(programZone, s -> new LayeredMap<>()).get(address);
    }

    public void setData(ProgramZone programZone, BigInteger address, ProgramData programData) {
        storage.computeIfAbsent(programZone, s -> new LayeredMap<>()).put(address, programData);
    }

    public BigInteger getOverflowLimit() {
        return overflowLimit;
    }

    public void setOverflowLimit(BigInteger overflowLimit) {
        this.overflowLimit = overflowLimit;
    }

    public BigInteger getUnderflowLimit() {
        return underflowLimit;
    }

    public void setUnderflowLimit(BigInteger underflowLimit) {
        this.underflowLimit = underflowLimit;
    }

    @Override
    public String toString() {
        return "ProgramContext{" +
                "jumping=" + jumping +
                ", exiting=" + exiting +
                ", instructionsExecuted=" + instructionsExecuted +
                ", instructionLimit=" + instructionLimit +
                ", instructionPosition=" + instructionPosition +
                ", stack=" + stack +
                ", stackLimit=" + stackLimit +
                ", storage=" + storage +
                ", overflowLimit=" + overflowLimit +
                ", underflowLimit=" + underflowLimit +
                '}';
    }
}
