package com.hileco.cortex.ethereum

import org.junit.Assert
import org.junit.Assume
import org.junit.Test

class SolidityCompilerTest {

    @Test
    fun testCompile() {
        Assume.assumeTrue(COMPILER_ENABLED)

        val bytecode = TEST_COMPILER.compile("barrier000.sol")

        Assert.assertTrue(bytecode.isNotEmpty())
    }

    @Test
    fun testAssembly() {
        Assume.assumeTrue(COMPILER_ENABLED)

        val output = TEST_COMPILER.execute("--bin", "--asm", "barrier000.sol")

        Assert.assertTrue(output.any { it.contains("EVM assembly") })
    }

    companion object {
        val TEST_COMPILER: SolidityCompiler
        val COMPILER_ENABLED = System.getProperty("os.name") == "Windows 10"

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