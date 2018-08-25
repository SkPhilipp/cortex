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

    @Override
    public void process(Tree tree) {
        // TODO: Parameters could also be LOAD.
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(treeNode -> treeNode.isInstruction(LOAD.class))
                .filter(treeNode -> treeNode.hasParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(treeNode -> {
                    var load = (LOAD) treeNode.getInstruction().get();
                    var push = (PUSH) treeNode.getParameters().get(0).getInstruction().get();
                    var address = new BigInteger(push.getBytes());
                    var programData = this.knownData.getOrDefault(load.getProgramStoreZone(), Collections.emptyMap()).get(address);
                    if (programData != null && this.knownSources.containsAll(programData.getSources())) {
                        treeNode.getInstruction().set(new NOOP());
                        treeNode.getInstruction().set(new PUSH(programData.getContent()));
                    }
                }));
    }
}
