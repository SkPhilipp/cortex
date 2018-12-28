package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import java.util.stream.Collectors

class KnownProcessor : Processor {
    private fun noopDownwards(graphNode: GraphNode) {
        unlinkParameters(graphNode)
        graphNode.instruction.set(NOOP())
        graphNode.parameters().forEach { noopDownwards(it) }
    }

    private fun unlinkParameters(graphNode: GraphNode) {
        val parameterEdges = graphNode.edges.stream()
                .filter { it is EdgeParameters }
                .collect(Collectors.toList())
        graphNode.edges.removeAll(parameterEdges)
    }

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.stream()
                    .filter { graphNode -> EdgeParameters.UTIL.count(graphNode) > 0 }
                    .filter { it.isSelfContained() }
                    .forEach { graphNode ->
                        val program = Program(graphNode.toInstructions())
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
                            graphNode.parameters().forEach { noopDownwards(it) }
                            unlinkParameters(graphNode)
                        }
                        // TODO: Replace the entire graphNode also when more instructions are available...
                    }
        }
    }
}
