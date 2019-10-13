package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.GraphBuilder.Companion.OPTIMIZED_GRAPH_BUILDER
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.fuzzing.ProgramGenerator
import org.junit.Test

class VisualGraphTest {
    @Test
    fun testMap() {
        val programGenerator = ProgramGenerator()
        val generated = programGenerator.generate(0)
        val first = generated.keySet().first()
        val program = generated[first]
        val instructions = program!!.instructions
        val basicGraph = BASIC_GRAPH_BUILDER.build(instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        val optimizedGraph = OPTIMIZED_GRAPH_BUILDER.build(instructions)
        val optimizedGraphVisualized = VisualGraph()
        optimizedGraphVisualized.map(optimizedGraph)
        Documentation.of(VisualGraph::class.java.simpleName)
                .headingParagraph(VisualGraph::class.java.simpleName)
                .paragraph("Program:").source(instructions)
                .paragraph("Visualization: (As basic graph)").image(basicGraphVisualized::render)
                .paragraph("Visualization: (As optimized graph)").image(optimizedGraphVisualized::render)
    }
}