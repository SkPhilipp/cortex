package com.hileco.cortex.tree.building;

import com.hileco.cortex.data.ProgramData;
import com.hileco.cortex.data.ProgramDataScope;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.InstructionsOptimizeStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadKnownProgramDataStrategy implements InstructionsOptimizeStrategy {

    private Map<String, Map<String, ProgramData>> knownData;
    private Set<ProgramDataScope> allowedScopes;

    public LoadKnownProgramDataStrategy(Map<String, Map<String, ProgramData>> knownData, Set<ProgramDataScope> allowedScopes) {
        this.knownData = knownData;
        this.allowedScopes = allowedScopes;
    }

    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            if (instruction.getOperation() instanceof Operations.Load
                    && instruction.getOperands() instanceof Operations.Load.Operands) {
                Operations.Load.Operands operands = (Operations.Load.Operands) instruction.getOperands();
                ProgramData programData = this.knownData.getOrDefault(operands.group, Collections.emptyMap()).get(operands.address);
                if (programData != null && allowedScopes.contains(programData.scope)) {
                    instructions.remove(i);
                    instructions.addAll(i, programBuilderFactory.builder()
                            .PUSH(programData.content)
                            .build());
                }
            }
        }
    }
}
