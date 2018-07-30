package com.hileco.cortex.optimizerlow.strategies;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.optimizerlow.InstructionsOptimizeStrategy;
import lombok.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
public class LoadKnownProgramDataStrategy implements InstructionsOptimizeStrategy {

    private final Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData;
    private final Set<ProgramDataSource> knownSources;

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
                    ProgramBuilder builder = programBuilderFactory.builder();
                    builder.NOOP();
                    builder.PUSH(programData.getContent());
                    instructions.addAll(i, builder.build().getInstructions());
                }
            }
        }
    }
}
