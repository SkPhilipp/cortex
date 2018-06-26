package com.hileco.cortex.optimizer;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.TreeBranch;

import java.util.ArrayList;
import java.util.List;

public class InstructionsOptimizer {

    private List<InstructionsOptimizeStrategy> strategies;
    private int passes;

    public InstructionsOptimizer() {
        this.strategies = new ArrayList<>();
        this.passes = 1;
    }

    public TreeBranch asTree(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        TreeBranch root = new TreeBranch();
        List<Instruction> list = new ArrayList<>(instructions);
        for (int i = 0; i < passes; i++) {
            strategies.forEach(instructionsOptimizeStrategy -> instructionsOptimizeStrategy.optimize(programBuilderFactory, list));
        }
        root.setInstructions(list);
        return root;
    }

    public void addStrategy(InstructionsOptimizeStrategy instructionsOptimizeStrategy) {
        strategies.add(instructionsOptimizeStrategy);
    }

    public void setPasses(int passes) {
        this.passes = passes;
    }
}
