package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.processors.*
import com.hileco.cortex.symbolic.instructions.Instruction

class GraphBuilder(val processors: List<Processor>) {
    fun build(instructions: List<Instruction>): Graph {
        val graph = Graph(instructions)
        processors.forEach { processor -> processor.process(graph) }
        return graph
    }

    companion object {
        val BASIC_GRAPH_BUILDER = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor()
        ))

        val OPTIMIZED_GRAPH_BUILDER = GraphBuilder(listOf(
                ParameterProcessor(),

                KnownLoadProcessor(mapOf()),

                DeadStartProcessor(),
                DeadEndProcessor(),
                DeadLoadProcessor(),
                DeadSaveProcessor(),
                DeadSwapProcessor(),

                JumpThreadingProcessor(),
                FlowProcessor(),
                JumpIllegalProcessor(),
                JumpUnreachableProcessor(),
                InstructionHoistProcessor(),
                InliningProcessor(),
                DeadPathConstraintProcessor(),

                ParameterProcessor(),
                FlowProcessor()
        ))
    }
}
