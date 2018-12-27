package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import lombok.Value
import java.math.BigInteger

@Value
class KnownLoadProcessor(private val knownData: Map<ProgramStoreZone, Map<BigInteger, BigInteger>>) : Processor {

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.stream()
                    .filter { it.isInstruction(LOAD::class.java) }
                    .filter { it.hasOneParameter(0) { parameter -> parameter.isInstruction(PUSH::class.java) } }
                    .forEach {
                        val pushGraphNode = it.parameters()[0]
                        val load = it.instruction.get() as LOAD
                        val push = pushGraphNode.instruction.get() as PUSH
                        val address = BigInteger(push.bytes)
                        knownData[load.programStoreZone]?.let { knownDataMap ->
                            knownDataMap[address]?.let { knownData: BigInteger ->
                                pushGraphNode.instruction.set(NOOP())
                                it.instruction.set(PUSH(knownData.toByteArray()))
                            }
                        }
                    }
        }
    }
}
