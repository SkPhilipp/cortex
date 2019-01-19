package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.analysis.GraphBuilder.Companion.OPTIMIZED_GRAPH_BUILDER
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.io.serialization.ProgramReferenceLoader
import com.hileco.cortex.vm.Program

class OptmizeCommand : CliktCommand(name = "optimize", help = "Optimize a sample and output the optmized graph instruction") {
    private val program: Program by option(help = "Cortex Assembly source file path").convert { ProgramReferenceLoader().load(it) }.required()

    override fun run() {
        val optimizedInstructions = execute(program)
        optimizedInstructions.forEach { echo(it) }
    }

    fun execute(program: Program): List<Instruction> {
        val graph = OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
        return graph.toInstructions()
    }
}
