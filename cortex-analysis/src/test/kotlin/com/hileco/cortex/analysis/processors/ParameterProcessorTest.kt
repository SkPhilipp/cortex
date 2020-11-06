package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_IF
import com.hileco.cortex.symbolic.instructions.stack.DUPLICATE
import com.hileco.cortex.symbolic.instructions.stack.PUSH
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
                PUSH(ONE_32),
                PUSH(10.toBackedInteger()),
                JUMP_IF()
        ))
        val graphBlocks = graph.graphBlocks
        val graphNodes = graphBlocks.first().graphNodes

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
                PUSH(ONE_32),
                PUSH(2.toBackedInteger()),
                PUSH(3.toBackedInteger()),
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
