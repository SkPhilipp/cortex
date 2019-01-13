package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP

class TrimEndProcessor : Processor {
    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            var trim = false
            graphBlock.graphNodes.forEach { graphNode ->
                if (trim) {
                    graphNode.instruction = NOOP()
                } else if (graphNode.instruction is JUMP
                        || graphNode.instruction is HALT
                        || graphNode.instruction is EXIT
                        || graphNode.instruction is CALL_RETURN) {
                    trim = true
                }
            }
        }
    }
}
