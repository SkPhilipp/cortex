package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.SWAP

class DeadSwapProcessor : Processor {

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.stream()
                    .filter { graphNode -> graphNode.isInstruction(SWAP::class.java) }
                    .filter { swapNode ->
                        val swap = swapNode.instruction.get() as SWAP
                        swap.topOffsetLeft == swap.topOffsetRight
                    }
                    .forEach { swapNode -> swapNode.instruction.set(NOOP()) }
        }
    }
}
