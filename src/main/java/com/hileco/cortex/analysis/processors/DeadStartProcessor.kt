package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.concrete.ProgramZone.MEMORY
import com.hileco.cortex.vm.concrete.ProgramZone.STACK

class DeadStartProcessor : Processor {
    private fun noopUpwards(startIndex: Int, graphBlock: GraphBlock) {
        for (i in startIndex downTo 0) {
            val graphNode = graphBlock.graphNodes[i]
            if (setOf(STACK, MEMORY).containsAll(graphNode.instruction.instructionModifiers) && graphNode.instruction !is JUMP_DESTINATION) {
                graphNode.instruction = NOOP()
            } else {
                break
            }
        }
    }

    override fun process(graph: Graph) {
        // upwards clean starting at exiting instructions
        graph.graphBlocks.forEach { graphBlock: GraphBlock ->
            val indexOfFirst = graphBlock.graphNodes.indexOfFirst {
                it.instruction is HALT || it.instruction is EXIT || it.instruction is CALL_RETURN
            }
            noopUpwards(indexOfFirst - 1, graphBlock)
        }
        // upwards clean starting at the last instruction
        val lastBlock = graph.graphBlocks.lastOrNull()
        if (lastBlock != null) {
            noopUpwards(lastBlock.graphNodes.size - 1, lastBlock)
        }
    }
}
