package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.processors.*
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.fuzzer.ProgramGenerator
import org.junit.Test
import java.io.IOException

class VisualGraphTest {
    @Test
    @Throws(IOException::class)
    fun testMap() {
        val programGenerator = ProgramGenerator()
        val generated = programGenerator.generate(0)
        val first = generated.keySet().iterator().next()
        val program = generated.get(first)
        val instructions = program!!.instructions
        val basicGraph = BASIC_GRAPH_BUILDER.build(instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        val optimizedGraph = OPTIMIZED_GRAPH_BUILDER.build(instructions)
        val optimizedGraphVisualized = VisualGraph()
        optimizedGraphVisualized.map(optimizedGraph)
        Documentation.of(VisualGraph::class.simpleName!!)
                .headingParagraph(VisualGraph::class.simpleName!!)
                .paragraph("Program:").source(instructions)
                .paragraph("Visualization: (As basic graph)").image(basicGraphVisualized.toBytes())
                .paragraph("Visualization: (As optimized graph)").image(optimizedGraphVisualized.toBytes())
    }

    companion object {
        private val BASIC_GRAPH_BUILDER = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor()
        ))
        private val OPTIMIZED_GRAPH_BUILDER = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                TrimEndProcessor(),
                DeadSwapProcessor(),
                JumpIllegalProcessor(),
                JumpThreadingProcessor(),
                JumpUnreachableProcessor(),
                KnownJumpIfProcessor(),
                KnownLoadProcessor(mapOf()),
                KnownProcessor(),
                FlowProcessor()
        ))
    }
}