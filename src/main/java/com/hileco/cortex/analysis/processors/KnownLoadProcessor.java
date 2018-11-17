package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.vm.data.ProgramData;
import com.hileco.cortex.vm.data.ProgramDataSource;
import com.hileco.cortex.vm.data.ProgramStoreZone;
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
    public void process(Graph graph) {
        // TODO: Parameters could also be LOAD.
        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                .filter(graphNode -> graphNode.isInstruction(LOAD.class))
                .filter(graphNode -> graphNode.hasOneParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(graphNode -> {
                    var load = (LOAD) graphNode.getInstruction().get();
                    var push = (PUSH) graphNode.getParameters().get(0).getInstruction().get();
                    var address = new BigInteger(push.getBytes());
                    var programData = this.knownData.getOrDefault(load.getProgramStoreZone(), Collections.emptyMap()).get(address);
                    if (programData != null && this.knownSources.containsAll(programData.getSources())) {
                        graphNode.getInstruction().set(new NOOP());
                        graphNode.getInstruction().set(new PUSH(programData.getContent()));
                    }
                }));
    }
}
