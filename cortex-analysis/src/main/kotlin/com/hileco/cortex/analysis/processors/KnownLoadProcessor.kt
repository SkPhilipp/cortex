package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.io.LOAD
import com.hileco.cortex.symbolic.instructions.stack.PUSH

class KnownLoadProcessor(private val knownData: Map<ProgramStoreZone, Map<BackedInteger, BackedInteger>>) : Processor {
    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { it.instruction is LOAD }
                    .filter { graph.edgeMapping.hasOneParameter(it, 0) { parameter -> parameter.instruction is PUSH } }
                    .forEach {
                        val pushGraphNode = graph.edgeMapping.parameters(it).first()
                        if (pushGraphNode != null) {
                            val load = it.instruction as LOAD
                            val push = pushGraphNode.instruction as PUSH
                            val address = push.value
                            knownData[load.programStoreZone]?.let { knownDataMap ->
                                knownDataMap[address]?.let { knownData ->
                                    pushGraphNode.instruction = NOOP()
                                    it.instruction = PUSH(knownData)
                                }
                            }
                        }
                    }
        }
    }
}
