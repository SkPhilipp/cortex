package com.hileco.cortex.external

import java.io.BufferedReader
import java.io.InputStreamReader


class SolidityCompiler(private val volume: String,
                       private val version: String = DEFAULT_VERSION) {

    fun compile(contractName: String): ByteArray {
        val process = ProcessBuilder("docker", "run", "-v", "$volume:/volume", "-w", "/volume", "ethereum/solc:$version", "--bin", contractName).start()
        val inputStream = InputStreamReader(process.inputStream)
        val inputBufferedReader = BufferedReader(inputStream)
        val errorStream = InputStreamReader(process.errorStream)
        val errorBufferedReader = BufferedReader(errorStream)
        process.waitFor()
        val errors = errorBufferedReader.lineSequence()
                .joinToString(separator = "\n") { it }
        if (errors.isNotEmpty()) {
            throw IllegalStateException(errors)
        }
        return inputBufferedReader.lineSequence()
                .filterNot {
                    it.isEmpty()
                            || it.startsWith("=======")
                            || it.startsWith("Binary:")
                }
                .map { it.deserializeBytes() }
                .single()
    }

    companion object {
        private const val DEFAULT_VERSION: String = "0.5.7"
    }
}