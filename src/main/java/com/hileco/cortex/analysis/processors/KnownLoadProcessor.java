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

@Value
public class KnownLoadProcessor implements Processor {
    private final Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData;
    private final Set<ProgramDataSource> knownSources;

    public void process(Tree tree) {
        // TODO: Parameters could also be LOAD.
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(treeNode -> treeNode.isInstruction(LOAD.class))
                .filter(treeNode -> treeNode.hasParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(treeNode -> {
                    LOAD load = (LOAD) treeNode.getInstruction().get();
                    PUSH push = (PUSH) treeNode.getParameters().get(0).getInstruction().get();
                    BigInteger address = new BigInteger(push.getBytes());
                    ProgramData programData = knownData.getOrDefault(load.getProgramStoreZone(), Collections.emptyMap()).get(address);
                    if (programData != null && knownSources.containsAll(programData.getSources())) {
                        treeNode.getInstruction().set(new NOOP());
                        treeNode.getInstruction().set(new PUSH(programData.getContent()));
                    }
                }));
    }
}
