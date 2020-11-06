package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class EthereumTranspilerTest {

    private val ethereumParser = EthereumParser()
    private val ethereumBarriers = EthereumBarriers()
    private val ethereumTranspiler = EthereumTranspiler()

    @Test
    fun test() {
        val bytecode = ethereumBarriers.byId("000").contractCode
        val ethereumInstructions = ethereumParser.parse(bytecode.deserializeBytes())
        val instructions = ethereumTranspiler.transpile(ethereumInstructions)

        Assert.assertTrue(instructions.any { it is PUSH })
    }

    @Test
    fun testTransaction() {
        val bytecode = ethereumBarriers.byId("000").contractCode
        val ethereumInstructions = ethereumParser.parse(bytecode.deserializeBytes())
        val instructions = ethereumTranspiler.transpile(ethereumInstructions)
        val basicGraph = BASIC_GRAPH_BUILDER.build(instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)

        Assert.assertTrue(instructions.first() is PUSH)
    }
}