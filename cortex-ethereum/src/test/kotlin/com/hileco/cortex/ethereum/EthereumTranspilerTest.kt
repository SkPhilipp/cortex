package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.stack.PUSH
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

        Documentation.of(EthereumTranspiler::class.java.simpleName)
                .headingParagraph(EthereumTranspiler::class.java.simpleName)
                .paragraph("Converts Ethereum instructions into Cortex instructions.")
                .paragraph("Using this on barrier000.sol's bytecode yields:")
                .source(instructions)

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

        Documentation.of(EthereumTranspiler::class.java.simpleName)
                .headingParagraph("Transaction Transpiling")
                .paragraph("Using this on bytecode of a contract creation transaction:")
                .source(bytecode)
                .paragraph("yields Cortex instructions:")
                .source(instructions)
                .paragraph("Visualization:").image(basicGraphVisualized::render)

        Assert.assertTrue(instructions.first() is PUSH)
    }
}