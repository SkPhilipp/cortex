package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.vm.ProgramStoreZone;
import lombok.Value;

import java.math.BigInteger;
import java.util.Map;

@Value
public class KnownLoadProcessor implements Processor {
    private final Map<ProgramStoreZone, Map<BigInteger, BigInteger>> knownData;

    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().forEach(graphBlock -> graphBlock.getGraphNodes().stream()
                .filter(graphNode -> graphNode.isInstruction(LOAD.class))
                .filter(graphNode -> graphNode.hasOneParameter(0, parameter -> parameter.isInstruction(PUSH.class)))
                .forEach(loadGraphNode -> {
                    var pushGraphNode = loadGraphNode.getParameters().get(0);
                    var load = (LOAD) loadGraphNode.getInstruction().get();
                    var push = (PUSH) pushGraphNode.getInstruction().get();
                    var address = new BigInteger(push.getBytes());
                    if (this.knownData.containsKey(load.getProgramStoreZone())) {
                        var knownDataMap = this.knownData.get(load.getProgramStoreZone());
                        if (knownDataMap != null && knownDataMap.containsKey(address)) {
                            pushGraphNode.getInstruction().set(new NOOP());
                            loadGraphNode.getInstruction().set(new PUSH(knownDataMap.get(address).toByteArray()));
                        }
                    }
                }));
    }
}
