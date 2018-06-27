package com.hileco.cortex.optimizer;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;

import java.util.List;

public interface InstructionsOptimizeStrategy {
    void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions);
}