package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

class KnownProcessor : Processor {
    private fun noopDownwards(edgeMapping: EdgeMapping, graphNode: GraphNode) {
        edgeMapping.remove(graphNode, EdgeParameters::class.java)
        graphNode.instruction.set(NOOP())
        edgeMapping.parameters(graphNode)
                .filterNotNull()
                .forEach { noopDownwards(edgeMapping, it) }
    }

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { graph.edgeMapping.get(it, EdgeParameters::class.java).count() > 0 }
                    .filter { graph.edgeMapping.isSelfContained(it) }
                    .forEach { graphNode ->
                        val program = Program(graph.edgeMapping.toInstructions(graphNode))
                        val programContext = ProgramContext(program)
                        val processContext = VirtualMachine(programContext)
                        val programRunner = ProgramRunner(processContext)
                        try {
                            programRunner.run()
                        } catch (e: ProgramException) {
                            throw IllegalStateException("Unknown cause for ProgramException", e)
                        }

                        val instructions = Sequence { programContext.stack.iterator() }
                                .map { PUSH(it) }
                                .toList()
                        if (instructions.size == 1) {
                            graphNode.instruction.set(instructions[0])
                            graph.edgeMapping.parameters(graphNode)
                                    .filterNotNull()
                                    .forEach { noopDownwards(graph.edgeMapping, it) }
                            graph.edgeMapping.remove(graphNode, EdgeParameters::class.java)
                        }
                        // TODO: Replace the entire graphNode also when more instructions are available...
                    }
        }
    }
}
