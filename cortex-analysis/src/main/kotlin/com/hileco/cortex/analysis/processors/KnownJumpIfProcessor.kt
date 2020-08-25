package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.vm.*
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF

class KnownJumpIfProcessor : Processor {
    override fun process(graph: Graph) {
        graph.graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { it.instruction is JUMP_IF }
                    .filter { graph.edgeMapping.hasOneParameter(it, JUMP_IF.CONDITION.position) { parameter -> graph.edgeMapping.isSelfContained(parameter) } }
                    .forEach {
                        val decidingNode = graph.edgeMapping.parameters(it).elementAt(1)
                        if (decidingNode != null) {
                            val program = Program(graph.edgeMapping.toInstructions(decidingNode))
                            val programContext = ProgramContext(program)
                            val virtualMachine = VirtualMachine(programContext)
                            val programRunner = ProgramRunner(virtualMachine)
                            try {
                                programRunner.run()
                            } catch (e: ProgramException) {
                                throw IllegalStateException("Unknown cause for ProgramException", e)
                            }
                            val result = programContext.stack.peek()
                            if (result > ZERO_32) {
                                graph.edgeMapping.fully(decidingNode) { node -> node.instruction = NOOP(); true }
                                it.instruction = JUMP()
                            } else {
                                graph.edgeMapping.fully(it) { node -> node.instruction = NOOP(); true }
                            }
                        }
                    }
        }
    }
}
