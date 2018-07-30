package com.hileco.cortex.optimizerlow;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;

import java.util.ArrayList;
import java.util.List;

public class InstructionsOptimizer {

    private List<InstructionsOptimizeStrategy> strategies;
    private int passes;

    public InstructionsOptimizer() {
        strategies = new ArrayList<>();
        passes = 1;
    }

    public List<Instruction> optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        List<Instruction> list = new ArrayList<>(instructions);
        for (int i = 0; i < passes; i++) {
            strategies.forEach(instructionsOptimizeStrategy -> instructionsOptimizeStrategy.optimize(programBuilderFactory, list));
        }
        return list;
    }

    public void addStrategy(InstructionsOptimizeStrategy instructionsOptimizeStrategy) {
        strategies.add(instructionsOptimizeStrategy);
    }

    public void setPasses(int passes) {
        this.passes = passes;
    }
}
