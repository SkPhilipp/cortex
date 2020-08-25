package com.hileco.cortex.ethereum

import com.hileco.cortex.collections.serialize
import com.hileco.cortex.documentation.Documentation
import org.junit.Assert
import org.junit.Test

class SolidityCompilerTest {

    @Test
    fun testCompile() {
        val bytecode = TEST_COMPILER.compile("barrier000.sol")

        Documentation.of(SolidityCompiler::class.java.simpleName)
                .headingParagraph(SolidityCompiler::class.java.simpleName)
                .paragraph("Allows for direct integration with the Solidity compiler (current default version: ${SolidityCompiler.DEFAULT_VERSION})." +
                        "This allows for testing on bytecode of contracts found in the wild, on different versions and across platforms.")
                .paragraph("Using the barrier000.sol example contract, the integration yields bytecode:")
                .source(bytecode.serialize())

        Assert.assertTrue(bytecode.isNotEmpty())
    }

    @Test
    fun testAssembly() {
        val output = TEST_COMPILER.execute("--bin", "--asm", "barrier000.sol")

        Assert.assertTrue(output.any { it.contains("EVM assembly") })
    }

    companion object {
        val TEST_COMPILER: SolidityCompiler

        init {
            val volumeBasePath = if (System.getProperty("os.name") == "Windows 10") {
                System.getProperty("user.dir")
                        .replace("\\", "/")
                        .replace("C:", "/c")
            } else {
                System.getProperty("user.dir")
            }
            TEST_COMPILER = SolidityCompiler("$volumeBasePath/src/main/resources/contracts")
        }
    }
}