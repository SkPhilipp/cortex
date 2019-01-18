package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

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
                    .filter { graph.edgeMapping.get(it, EdgeParameters::class.java).count() > 0 }
                    .filter { graph.edgeMapping.isSelfContained(it) }
                    .forEach { graphNode ->
                        val program = Program(graph.edgeMapping.toInstructions(graphNode))
                        val programContext = ProgramContext(program)
                        val virtualMachine = VirtualMachine(programContext)
                        val programRunner = ProgramRunner(virtualMachine)
                        programRunner.run()
                        val stackElement = Sequence { programContext.stack.iterator() }.singleOrNull()
                        if (stackElement != null) {
                            graphNode.instruction = PUSH(stackElement)
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
