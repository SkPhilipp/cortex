package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramRunner
import com.hileco.cortex.vm.concrete.VirtualMachine

class KnownProcessor : Processor {
    private fun noopDownwards(edgeMapping: EdgeMapping, graphNode: GraphNode) {
        edgeMapping.remove(graphNode, EdgeParameters::class.java)
        graphNode.instruction = NOOP()
        edgeMapping.parameters(graphNode)
                .filterNotNull()
                .forEach { noopDownwards(edgeMapping, it) }
    }

    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { graph.edgeMapping.isSelfContained(it) }
                    .forEach { graphNode ->
                        val instructions = graph.edgeMapping.toInstructions(graphNode)
                        if (instructions.none { it is DUPLICATE && it.topOffset > 0 }) {
                            val program = Program(instructions)
                            val programContext = ProgramContext(program)
                            val virtualMachine = VirtualMachine(programContext)
                            val programRunner = ProgramRunner(virtualMachine)
                            programRunner.run()
                            // TODO: Replace the entire graphNode also when more instructions are available...
                            val stackElement = programContext.stack.asSequence().singleOrNull()
                            if (stackElement != null) {
                                graphNode.instruction = PUSH(stackElement)
                                graph.edgeMapping.parameters(graphNode)
                                        .filterNotNull()
                                        .forEach { noopDownwards(graph.edgeMapping, it) }
                                graph.edgeMapping.remove(graphNode, EdgeParameters::class.java)
                            }
                        }
                    }
        }
    }
}
