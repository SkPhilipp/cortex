package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

class KnownJumpIfProcessor : Processor {
    private fun fully(graphNode: GraphNode, consumer: (GraphNode) -> Unit) {
        consumer(graphNode)
        for (parameter in graphNode.parameters()) {
            if (parameter != null) {
                fully(parameter, consumer)
            }
        }
    }

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { it.instruction.get() is JUMP_IF }
                    .filter { it.hasOneParameter(JUMP_IF.CONDITION.position) { parameter -> parameter.isSelfContained() } }
                    .forEach {
                        val decidingNode = it.parameters()[1]
                        if (decidingNode != null) {
                            val program = Program(decidingNode.toInstructions())
                            val programContext = ProgramContext(program)
                            val processContext = VirtualMachine(programContext)
                            val programRunner = ProgramRunner(processContext)
                            try {
                                programRunner.run()
                            } catch (e: ProgramException) {
                                throw IllegalStateException("Unknown cause for ProgramException", e)
                            }
                            val result = programContext.stack.peek()
                            if (BigInteger(result) > BigInteger.ZERO) {
                                fully(decidingNode) { node -> node.instruction.set(NOOP()) }
                                it.instruction.set(JUMP())
                            } else {
                                fully(it) { node -> node.instruction.set(NOOP()) }
                            }
                        }
                    }
        }
    }
}
