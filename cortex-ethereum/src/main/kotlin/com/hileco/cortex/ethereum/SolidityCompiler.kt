package com.hileco.cortex.ethereum

import com.hileco.cortex.ethereum.process.ProcessRunner


class SolidityCompiler(private val volume: String,
                       private val version: String = DEFAULT_VERSION) {
    private val processRunner = ProcessRunner()

    fun execute(vararg args: String): Sequence<String> {
        val command = arrayListOf("docker", "run", "-v", "$volume:/volume", "-w", "/volume", "ethereum/solc:$version")
        command.addAll(args)
        val processResult = processRunner.execute(command)

        if (processResult.exitCode > 0) {
            throw IllegalStateException("Error from ${command.joinToString(separator = " ")}:\n ${processResult.errors.joinToString()}")
        }
        return processResult.output.asSequence()
    }

    fun compile(contractName: String): ByteArray {
        return execute("--bin", contractName)
                .filterNot {
                    it.isEmpty()
                            || it.startsWith("=======")
                            || it.startsWith("Binary:")
                }
                .map { it.deserializeBytes() }
                .single()
    }

    companion object {
        const val DEFAULT_VERSION: String = "0.5.7"
    }
}