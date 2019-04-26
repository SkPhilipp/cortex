package com.hileco.cortex.external

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.external.SolidityCompilerTest.Companion.TEST_COMPILER
import org.junit.Assert
import org.junit.Test

class EthereumParserTest {
    @Test
    fun parse() {
        val bytecode = TEST_COMPILER.compile("05_greeter.sol")
        val ethereumParser = EthereumParser()
        val instructions = ethereumParser.parse(bytecode)

        Documentation.of(EthereumParser::class.java.simpleName)
                .headingParagraph(EthereumParser::class.java.simpleName)
                .paragraph("Converts Ethereum bytecode into Ethereum instructions.")
                .paragraph("Using this on 05_greeter.sol's bytecode yields instructions:")
                .source(instructions)

        Assert.assertEquals(instructions.first(), EthereumInstruction(EthereumOperation.PUSH1, "80".deserializeBytes()))
    }
}