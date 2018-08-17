package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import lombok.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.hileco.cortex.tree.stream.Filters.instruction;
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
                .filter(instruction(LOAD.class))
                .filter(parameter(0, instruction(PUSH.class)))
                .forEach(programNode -> {
                    ProgramNode parameterNode = programNode.getParameters().get(0);
                    PUSH push = (PUSH) parameterNode.getInstruction();
                    LOAD load = (LOAD) programNode.getInstruction();
                    BigInteger address = new BigInteger(push.getBytes());
                    ProgramData programData = knownData.getOrDefault(load.getProgramStoreZone(), Collections.emptyMap()).get(address);
                    if (programData != null && knownSources.containsAll(programData.getSources())) {
                        ProgramBuilder builder = programBuilderFactory.builder();
                        parameterNode.setInstruction(builder.NOOP().get());
                        programNode.setInstruction(builder.PUSH(programData.getContent()).get());
                    }
                });
    }
}
