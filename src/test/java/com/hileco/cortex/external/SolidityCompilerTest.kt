package com.hileco.cortex.external

import org.junit.Assert
import org.junit.Test

class SolidityCompilerTest {
    @Test
    fun compile() {
        val solidityCompiler = SolidityCompiler(TEST_VOLUME)
        val bytecode = solidityCompiler.compile("05_greeter.sol")
        bytecode.forEach {
            val operation = EthereumOperation.ofCode(it)
            if (operation == null) {
                println("UNKNOWN (${it.serialize()})")
            } else {
                println(operation)
            }
        }
        Assert.assertTrue(bytecode.isNotEmpty())
    }

    private val TEST_VOLUME: String

    init {
        val volumeBasePath = if (System.getProperty("os.name") == "Windows 10") {
            System.getProperty("user.dir")
                    .replace("\\", "/")
                    .replace("C:", "/c")
        } else {
            System.getProperty("user.dir")
        }
        TEST_VOLUME = "$volumeBasePath/src/test/resources/contracts"
    }
}