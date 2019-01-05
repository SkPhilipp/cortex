package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.instructions.debug.NOOP
import java.util.*

class JumpUnreachableProcessor : Processor {
    override fun process(graph: Graph) {
        graph.edgeMapping.get(EdgeFlowMapping::class.java).first().let {
            val targets = HashSet<Int>()
            targets.add(PROGRAM_START)
            targets.addAll(it.flowsToTarget.keys.filterNotNull())
            graph.graphBlocks.forEach { graphBlock ->
                graphBlock.graphNodes.firstOrNull()?.let { startingNode ->
                    if (!targets.contains(startingNode.line)) {
                        graphBlock.graphNodes.forEach { graphNode -> graphNode.instruction = NOOP() }
                    }
                }
            }
        }
    }

    companion object {
        private const val PROGRAM_START = 0
    }
}
