package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.SWAP

class DeadSwapProcessor : Processor {
    override fun process(graph: Graph) {
        graph.graphBlocks.flatMap { it.graphNodes }
                .filter {
                    val instruction = it.instruction
                    instruction is SWAP && instruction.topOffsetLeft == instruction.topOffsetRight
                }
                .forEach { it.instruction = NOOP() }
    }
}

