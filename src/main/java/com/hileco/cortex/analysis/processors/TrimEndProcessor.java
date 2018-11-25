package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.instructions.calls.CALL_RETURN;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;

import java.util.concurrent.atomic.AtomicBoolean;

public class TrimEndProcessor implements Processor {

    private static final Class<?>[] GUARANTEED_ENDS = {JUMP.class, HALT.class, EXIT.class, CALL_RETURN.class};

    @Override
    public void process(Graph graph) {
        graph.getGraphBlocks().forEach(graphBlock -> {
            var trim = new AtomicBoolean(false);
            graphBlock.getGraphNodes().forEach(graphNode -> {
                var instruction = graphNode.getInstruction();
                if (trim.get()) {
                    instruction.set(new NOOP());
                } else if (graphNode.isInstruction(GUARANTEED_ENDS)) {
                    trim.set(true);
                }

            });
        });
    }
}
