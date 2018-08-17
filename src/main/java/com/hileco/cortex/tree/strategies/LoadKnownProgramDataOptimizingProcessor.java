package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Operations.Load;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import lombok.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.hileco.cortex.instructions.Operations.Push;
import static com.hileco.cortex.tree.stream.Filters.operation;
import static com.hileco.cortex.tree.stream.Filters.parameter;

@Value
public class LoadKnownProgramDataOptimizingProcessor implements ProgramTreeProcessor {

    private final ProgramBuilderFactory programBuilderFactory;
    private final Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData;
    private final Set<ProgramDataSource> knownSources;

    @Override
    public void process(ProgramTree programTree) {
        programTree.getNodes()
                .stream()
                .filter(operation(Load.class))
                .filter(parameter(0, operation(Push.class)))
                .forEach(programNode -> {
                    ProgramNode parameterNode = programNode.getParameters().get(0);
                    Push.Operands pushOperands = (Push.Operands) parameterNode.getInstruction().getOperands();
                    Load.Operands loadOperands = (Load.Operands) programNode.getInstruction().getOperands();
                    BigInteger address = new BigInteger(pushOperands.bytes);
                    ProgramData programData = knownData.getOrDefault(loadOperands.programStoreZone, Collections.emptyMap()).get(address);
                    if (programData != null && knownSources.containsAll(programData.getSources())) {
                        ProgramBuilder builder = programBuilderFactory.builder();
                        parameterNode.setInstruction(builder.NOOP().get());
                        programNode.setInstruction(builder.PUSH(programData.getContent()).get());
                    }
                });
    }
}
