package com.hileco.cortex.fuzzer.choices;

public enum FuzzProgramLayout implements Chanced {
    FUNCTION_TABLE(12),
    FUNCTION(1D);

    private double chance;

    FuzzProgramLayout(double chance) {
        this.chance = chance;
    }

    @Override
    public double chance() {
        return chance;
    }
}
