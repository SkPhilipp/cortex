package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.analysis.GraphBuilder.Companion.OPTIMIZED_GRAPH_BUILDER
import com.hileco.cortex.analysis.attack.Attacker
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.io.serialization.ProgramReferenceLoader
import com.hileco.cortex.vm.Program
import java.util.*


class AttackCommand : CliktCommand(name = "attack", help = "Attack a given sample, outputting a solution JSON") {
    private val program: Program by option(help = "Cortex Assembly program file path").convert { ProgramReferenceLoader().load(it) }.required()
    private val method: String by option(help = "Attack method").default(METHOD_WINNER)

    override fun run() {
        val solutions = execute(method, program)
        echo(Commands.OBJECT_MAPPER.writeValueAsString(solutions))
    }

    fun execute(method: String, program: Program): ArrayList<Solution> {
        val graph = OPTIMIZED_GRAPH_BUILDER.build(program.instructions)
        val targetPredicate = when (method) {
            METHOD_WINNER -> Attacker.TARGET_IS_HALT_WINNER
            else -> throw IllegalArgumentException("Invalid method")
        }
        val attacker = Attacker(targetPredicate)
        return attacker.solve(graph)
    }

    companion object {
        const val METHOD_WINNER = "WINNER"
    }
}
