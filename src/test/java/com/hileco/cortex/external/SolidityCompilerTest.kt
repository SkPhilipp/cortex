package com.hileco.cortex.external

import org.junit.Assert
import org.junit.Test

class SolidityCompilerTest {
    @Test
    fun testCompile() {
        val bytecode = TEST_COMPILER.compile("05_greeter.sol")
        Assert.assertTrue(bytecode.isNotEmpty())
    }

    @Test
    fun testAssembly() {
        val output = TEST_COMPILER.execute("--bin", "--asm", "05_greeter.sol")
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
            TEST_COMPILER = SolidityCompiler("$volumeBasePath/src/test/resources/contracts")
        }
    }
}