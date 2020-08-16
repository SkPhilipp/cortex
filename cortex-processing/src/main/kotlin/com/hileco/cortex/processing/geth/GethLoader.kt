package com.hileco.cortex.processing.geth

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

open class GethLoader {
    private val objectMapper = ObjectMapper()
    private val processRunner = ProcessRunner()

    private fun read(gethScriptOutput: List<String>): JsonNode {
        val taken = gethScriptOutput.takeWhile { !it.startsWith("___END___") }
                .joinToString("\n")
        return objectMapper.readTree(taken)
    }

    /**
     * Executes a script's 'run' function for geth in the docker container 'miner'.
     *
     * Note that [script] and all keys and values of [parameterList] become part of executable commands and should only contain safe [a-Z0-9] characters.
     *
     * @param script A script local to the docker container 'miner'
     * @param parameterList Parameters for the 'run' function of the script.
     */
    fun executeGeth(script: String, vararg parameterList: Any): JsonNode {
        val parameters = parameterList.joinToString { if (it is String) "'$it'" else "$it" }
        val command = listOf("docker", "exec", "miner", "geth", "attach", "--exec", "loadScript(\'/scripts/$script\');run($parameters)")
        val processResult = processRunner.execute(command)
        if (processResult.exitCode > 0 && processResult.errors.isNotEmpty()) {
            val commandText = command.joinToString(separator = " ")
            val errorText = processResult.errors.joinToString(separator = "\n")
            throw IllegalStateException("Error from $commandText:\n $errorText")
        }
        return read(processResult.output)
    }
}
