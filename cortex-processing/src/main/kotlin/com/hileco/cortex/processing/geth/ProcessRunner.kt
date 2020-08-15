package com.hileco.cortex.processing.geth

import java.io.BufferedReader
import java.io.InputStreamReader

class ProcessRunner {
    fun execute(command: List<String>): ProcessResult {
        val processBuilder = ProcessBuilder(command)
        val process = processBuilder.start()
        var output: List<String> = listOf()
        val outputThread = Thread {
            val inputStream = InputStreamReader(process.inputStream)
            val inputBufferedReader = BufferedReader(inputStream)
            output = inputBufferedReader.readLines()
        }
        var errors: List<String> = listOf()
        val errorsThread = Thread {
            val errorStream = InputStreamReader(process.errorStream)
            val errorBufferedReader = BufferedReader(errorStream)
            errors = errorBufferedReader.readLines()
        }
        outputThread.start()
        errorsThread.start()
        val exitCode = process.waitFor()
        outputThread.join()
        errorsThread.join()
        return ProcessResult(
                output = output,
                errors = errors,
                exitCode = exitCode
        )
    }
}
