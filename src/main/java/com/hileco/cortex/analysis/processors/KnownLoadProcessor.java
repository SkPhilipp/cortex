package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.stack.PUSH;
import lombok.Value;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.hileco.cortex.analysis.Predicates.instruction;
import static com.hileco.cortex.analysis.Predicates.parameter;
import static com.hileco.cortex.analysis.Predicates.type;

@Value
public class KnownLoadProcessor implements Processor {
    private final Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData;
    private final Set<ProgramDataSource> knownSources;

    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(instruction(type(LOAD.class)))
                .filter(parameter(0, instruction(type(PUSH.class))))
                .forEach(treeNode -> {
                    LOAD load = (LOAD) treeNode.getInstruction();
                    PUSH push = (PUSH) treeNode.getParameters().get(0).getInstruction();
                    BigInteger address = new BigInteger(push.getBytes());
                    ProgramData programData = knownData.getOrDefault(load.getProgramStoreZone(), Collections.emptyMap()).get(address);
                    if (programData != null && knownSources.containsAll(programData.getSources())) {
                        // TODO: Replace the instructions within the block and tree as well...
                        treeNode.setInstruction(new NOOP());
                        treeNode.setInstruction(new PUSH(programData.getContent()));
                    }
                }));
    }
}
