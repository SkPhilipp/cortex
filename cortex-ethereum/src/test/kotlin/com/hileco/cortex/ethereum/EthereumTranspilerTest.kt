package com.hileco.cortex.ethereum

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class EthereumTranspilerTest {
    @Test
    fun test() {
        val bytecode = SolidityCompilerTest.TEST_COMPILER.compile("05_greeter.sol")
        val ethereumParser = EthereumParser()
        val ethereumInstructions = ethereumParser.parse(bytecode)
        val ethereumTranspiler = EthereumTranspiler()
        val instructions = ethereumTranspiler.transpile(ethereumInstructions)

        Documentation.of(EthereumTranspiler::class.java.simpleName)
                .headingParagraph(EthereumTranspiler::class.java.simpleName)
                .paragraph("Converts Ethereum instructions into Cortex instructions.")
                .paragraph("Using this on 05_greeter.sol's bytecode yields:")
                .source(instructions)

        Assert.assertTrue(instructions.any { it is PUSH })
    }
}