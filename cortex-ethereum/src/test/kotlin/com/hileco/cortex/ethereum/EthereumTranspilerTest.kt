package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class EthereumTranspilerTest {

    private val ethereumParser = EthereumParser()
    private val ethereumTranspiler = EthereumTranspiler()

    @Test
    fun test() {
        val bytecode = SolidityCompilerTest.TEST_COMPILER.compile("barrier000.sol")
        val ethereumInstructions = ethereumParser.parse(bytecode)
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
        val bytecode = "6060604052341561000f57600080fd5b604051602080610149833981016040528080519060200190919050505b806000819055505b505b610104806100456000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680632a1afcd914605157806360fe47b11460775780636d4ce63c146097575b600080fd5b3415605b57600080fd5b606160bd565b6040518082815260200191505060405180910390f35b3415608157600080fd5b6095600480803590602001909190505060c3565b005b341560a157600080fd5b60a760ce565b6040518082815260200191505060405180910390f35b60005481565b806000819055505b50565b6000805490505b905600a165627a7a72305820d5851baab720bba574474de3d09dbeaabc674a15f4dd93b974908476542c23f00029"
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