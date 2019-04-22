package com.hileco.cortex.external

import com.hileco.cortex.external.SolidityCompilerTest.Companion.TEST_COMPILER
import org.junit.Assert
import org.junit.Test

class EthereumParserTest {
    @Test
    fun parse() {
        val ethereumParser = EthereumParser()
        val bytecode = TEST_COMPILER.compile("05_greeter.sol")
        val instructions = ethereumParser.parse(bytecode)
        Assert.assertEquals(instructions.first(), EthereumInstruction(EthereumOperation.PUSH1, "80".deserializeBytes()))
    }
}