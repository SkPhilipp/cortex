package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger

class KnownLoadProcessor(private val knownData: Map<ProgramStoreZone, Map<BigInteger, BigInteger>>) : Processor {
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
                            val address = BigInteger(push.bytes)
                            knownData[load.programStoreZone]?.let { knownDataMap ->
                                knownDataMap[address]?.let { knownData: BigInteger ->
                                    pushGraphNode.instruction = NOOP()
                                    it.instruction = PUSH(knownData.toByteArray())
                                }
                            }
                        }
                    }
        }
    }
}
