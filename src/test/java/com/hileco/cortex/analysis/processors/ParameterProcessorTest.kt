package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.util.*

class ParameterProcessorTest : ProcessorFuzzTest() {

    @Test
    fun process() {
        val processors = ArrayList<Processor>()
        processors.add(ParameterProcessor())
        val graphBuilder = GraphBuilder(processors)
        val graph = graphBuilder.build(listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                JUMP_IF()
        ))
        val graphBlocks = graph.graphBlocks
        val graphNodes = graphBlocks[0].graphNodes
        Documentation.of(ParameterProcessor::class.simpleName!!)
                .headingParagraph(ParameterProcessor::class.simpleName!!)
                .paragraph("Adds edges describing the instructions used as input for every other instruction.")
        Assert.assertEquals(1L, EdgeParameterConsumer.UTIL.count(graphNodes[0]))
        Assert.assertEquals(1L, EdgeParameterConsumer.UTIL.count(graphNodes[1]))
        val edgeParameters = EdgeParameters.UTIL.findAny(graphNodes[2])
        Assert.assertTrue(edgeParameters != null)
        Assert.assertEquals(2, edgeParameters!!.graphNodes.size.toLong())
    }

    override fun fuzzTestableProcessor(): Processor {
        return ParameterProcessor()
    }
}
