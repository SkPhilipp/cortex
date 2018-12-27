package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP

import java.util.concurrent.atomic.AtomicBoolean

class TrimEndProcessor : Processor {

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            val trim = AtomicBoolean(false)
            graphBlock.graphNodes.forEach { graphNode ->
                val instruction = graphNode.instruction
                if (trim.get()) {
                    instruction.set(NOOP())
                } else if (graphNode.isInstruction(*GUARANTEED_ENDS)) {
                    trim.set(true)
                }

            }
        }
    }

    companion object {
        private val GUARANTEED_ENDS = arrayOf(
                JUMP::class.java,
                HALT::class.java,
                EXIT::class.java,
                CALL_RETURN::class.java
        )
    }
}
