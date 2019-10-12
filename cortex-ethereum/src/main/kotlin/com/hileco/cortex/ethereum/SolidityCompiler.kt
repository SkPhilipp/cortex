package com.hileco.cortex.ethereum

import java.io.BufferedReader
import java.io.InputStreamReader


class SolidityCompiler(private val volume: String,
                       private val version: String = DEFAULT_VERSION) {
    fun execute(vararg args: String): Sequence<String> {
        val command = arrayListOf("docker", "run", "-v", "$volume:/volume", "-w", "/volume", "ethereum/solc:$version")
        command.addAll(args)
        val processBuilder = ProcessBuilder(command)
        val process = processBuilder.start()
        var output: List<String>? = null
        var errors: List<String>? = null
        val outputThread = Thread {
            val inputStream = InputStreamReader(process.inputStream)
            val inputBufferedReader = BufferedReader(inputStream)
            output = inputBufferedReader.lineSequence().toList()
        }
        val errorsThread = Thread {
            val errorStream = InputStreamReader(process.errorStream)
            val errorBufferedReader = BufferedReader(errorStream)
            errors = errorBufferedReader.lineSequence().toList()
        }
        outputThread.start()
        errorsThread.start()
        process.waitFor()
        outputThread.join()
        errorsThread.join()
        val errorsResult = errors ?: listOf()
        val outputResult = output ?: listOf()
        if (errorsResult.isNotEmpty()) {
            throw IllegalStateException("Error from ${command.joinToString(separator = " ")}:\n ${errorsResult.joinToString()}")
        }
        return outputResult.asSequence()
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