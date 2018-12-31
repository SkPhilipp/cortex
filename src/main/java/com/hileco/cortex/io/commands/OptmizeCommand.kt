package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.io.commands.Commands.Companion.OPTIMIZED_GRAPH_BUILDER
import com.hileco.cortex.io.serialization.InstructionParser
import java.io.File
import java.io.InputStream

class OptmizeCommand : CliktCommand(name = "analyze", help = "Optimize a sample and output the optmized graph instruction") {
    private val source: File? by option(help = "Cortex Assembly source file path, defaults to stdin").file()

    override fun run() {
        val optimizedInstructions = execute(source?.inputStream() ?: System.`in`)
        optimizedInstructions.forEach { echo(it) }
    }

    fun execute(instructionStream: InputStream): List<Instruction> {
        val instructionParser = InstructionParser()
        val instructions = instructionStream.reader().readLines().map { instructionParser.parse(it) }
        val graph = OPTIMIZED_GRAPH_BUILDER.build(instructions)
        return graph.toInstructions()
    }
}
