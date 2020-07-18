package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.vm.instructions.debug.NOOP
import java.util.*

class JumpUnreachableProcessor : Processor {
    override fun process(graph: Graph) {
        graph.edgeMapping.get(FlowMapping::class.java).first().let {
            val targets = HashSet<Int>()
            targets.add(PROGRAM_START)
            targets.addAll(it.flowsToTarget.keys.filterNotNull())
            val unreachableBlocks = graph.graphBlocks.filterNot { graphBlock ->
                val startNode = graphBlock.graphNodes.first()
                targets.contains(startNode.position)
            }
            unreachableBlocks.forEach { graphBlock ->
                graphBlock.graphNodes.forEach { graphNode -> graphNode.instruction = NOOP() }
                graph.mergeUpwards(graphBlock)
            }
        }
    }

    companion object {
        private const val PROGRAM_START = 0
    }
}
