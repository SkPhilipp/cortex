package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.stack.DUPLICATE
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.util.*

class ParameterProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val processors = ArrayList<Processor>()
        processors.add(ParameterProcessor())
        val graphBuilder = GraphBuilder(processors)
        val graph = graphBuilder.build(listOf(
                PUSH(1),
                PUSH(10),
                JUMP_IF()
        ))
        val graphBlocks = graph.graphBlocks
        val graphNodes = graphBlocks.first().graphNodes
        Documentation.of(ParameterProcessor::class.java.simpleName)
                .headingParagraph(ParameterProcessor::class.java.simpleName)
                .paragraph("Adds edges describing the instructions used as input for every other instruction.")
        Assert.assertEquals(1, graph.edgeMapping.get(graphNodes[0], EdgeParameterConsumer::class.java).count())
        Assert.assertEquals(1, graph.edgeMapping.get(graphNodes[1], EdgeParameterConsumer::class.java).count())
        val edgeParameters = graph.edgeMapping.get(graphNodes[2], EdgeParameters::class.java).first()
        Assert.assertEquals(2, edgeParameters.graphNodes.size)
    }

    @Test
    fun processDuplicate() {
        val processors = ArrayList<Processor>()
        processors.add(ParameterProcessor())
        val graphBuilder = GraphBuilder(processors)
        val graph = graphBuilder.build(listOf(
                PUSH(1),
                PUSH(2),
                PUSH(3),
                DUPLICATE(1)
        ))
        val graphBlocks = graph.graphBlocks
        val graphNodes = graphBlocks[0].graphNodes
        Assert.assertEquals(1, graph.edgeMapping.get(graphNodes[1], EdgeParameterConsumer::class.java).count())
        val edgeParameters = graph.edgeMapping.get(graphNodes[3], EdgeParameters::class.java).first()
        Assert.assertEquals(1, edgeParameters.graphNodes.size)
    }

    override fun fuzzTestableProcessor(): Processor {
        return ParameterProcessor()
    }
}
