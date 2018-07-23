package com.hileco.cortex.optimizerlow.strategies;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.optimizerlow.InstructionsOptimizeStrategy;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadKnownProgramDataStrategy implements InstructionsOptimizeStrategy {

    private Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData;
    private Set<ProgramDataSource> knownSources;

    public LoadKnownProgramDataStrategy(Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData, Set<ProgramDataSource> knownSources) {
        this.knownData = knownData;
        this.knownSources = knownSources;
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
                ProgramData programData = knownData.getOrDefault(loadOperands.programStoreZone, Collections.emptyMap()).get(address);
                if (programData != null && knownSources.containsAll(programData.getSources())) {
                    instructions.remove(i + 1);
                    instructions.remove(i);
                    instructions.addAll(i, programBuilderFactory.builder()
                            .NOOP()
                            .PUSH(programData.getContent())
                            .build()
                            .getInstructions());
                }
            }
        }
    }
}
