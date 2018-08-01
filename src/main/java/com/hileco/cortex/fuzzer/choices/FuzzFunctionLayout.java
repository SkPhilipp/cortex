package com.hileco.cortex.fuzzer.choices;

public enum FuzzFunctionLayout implements Chanced {
    EXIT(0.3D),
    RETURN(3D),
    CALL_WITH_FUNDS(1D),
    CALL_LIBRARY(1D),
    SAVE(3D);

    private double chance;

    FuzzFunctionLayout(double chance) {
        this.chance = chance;
    }

    @Override
    public double chance() {
        return chance;
    }
}
