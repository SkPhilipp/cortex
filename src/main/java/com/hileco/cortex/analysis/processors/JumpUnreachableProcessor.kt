package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.instructions.debug.NOOP
import java.util.*

class JumpUnreachableProcessor : Processor {

    override fun process(graph: Graph) {
        EdgeFlowMapping.UTIL.findAny(graph)?.let {
            val targets = HashSet<Int>()
            targets.add(PROGRAM_START)
            it.flowsToTarget.keys.forEach { target ->
                if (target != null) {
                    targets.add(target)
                }
            }
            graph.graphBlocks.forEach { graphBlock ->
                graphBlock.graphNodes.stream().findFirst().ifPresent { startingNode ->
                    if (!targets.contains(startingNode.line)) {
                        graphBlock.graphNodes.forEach { graphNode -> graphNode.instruction.set(NOOP()) }
                    }
                }
            }
        }
    }

    companion object {
        private const val PROGRAM_START = 0
    }
}
