package com.hileco.cortex.optimizer.strategies;

import com.hileco.cortex.data.ProgramData;
import com.hileco.cortex.data.ProgramDataScope;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.optimizer.InstructionsOptimizeStrategy;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadKnownProgramDataStrategy implements InstructionsOptimizeStrategy {

    private Map<String, Map<BigInteger, ProgramData>> knownData;
    private Set<ProgramDataScope> allowedScopes;

    public LoadKnownProgramDataStrategy(Map<String, Map<BigInteger, ProgramData>> knownData, Set<ProgramDataScope> allowedScopes) {
        this.knownData = knownData;
        this.allowedScopes = allowedScopes;
    }

    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        for (int i = 0; i + 1 < instructions.size(); i++) {
            Instruction push = instructions.get(i);
            Instruction load = instructions.get(i + 1);
            if (push.getOperation() instanceof Operations.Push
                    && push.getOperands() instanceof Operations.Push.Operands
                    && load.getOperation() instanceof Operations.Load
                    && load.getOperands() instanceof Operations.Load.Operands) {
                Operations.Push.Operands pushOperands = (Operations.Push.Operands) push.getOperands();
                Operations.Load.Operands loadOperands = (Operations.Load.Operands) load.getOperands();
                BigInteger address = new BigInteger(pushOperands.bytes);
                ProgramData programData = this.knownData.getOrDefault(loadOperands.group, Collections.emptyMap()).get(address);
                if (programData != null && allowedScopes.contains(programData.scope)) {
                    instructions.remove(i + 1);
                    instructions.remove(i);
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .PUSH(programData.content)
                            .build());
                }
            }
        }
    }
}
