package com.hileco.cortex.processing.geth

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

open class GethLoader {
    private val objectMapper = ObjectMapper()
    private val processRunner = ProcessRunner()

    fun unpackage(resourcePath: String): Path {
        val tempDir = System.getProperty("java.io.tmpdir")
        val targetFileName = Path.of(resourcePath).fileName.toString()
        val targetPath = Path.of(tempDir, targetFileName)
        val resource = this.javaClass.getResourceAsStream(resourcePath)
        Files.copy(resource, targetPath, StandardCopyOption.REPLACE_EXISTING)
        return targetPath
    }

    private fun read(gethScriptOutput: List<String>): JsonNode {
        val taken = gethScriptOutput.takeWhile { !it.startsWith("___END___") }
                .joinToString("\n")
        return objectMapper.readTree(taken)
    }

    fun executeGethScript(script: Path, parameters: Map<String, String>): JsonNode {
        // TODO: Pass in network model arguments
        val command = listOf("docker-compose", "exec", "miner", "geth", "attach", "--exec", "'loadScript(\"$script\")")
        val processResult = processRunner.execute(command)
        if (processResult.exitCode > 0 && processResult.errors.isNotEmpty()) {
            val commandText = command.joinToString(separator = " ")
            val errorText = processResult.errors.joinToString(separator = "\n")
            throw IllegalStateException("Error from $commandText:\n $errorText")
        }
        return read(processResult.output)
    }
}
