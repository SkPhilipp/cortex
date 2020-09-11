package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.NetworkModel

class GraphCommand : CliktCommand(name = "graph", help = "Print the analysis graph for a program") {
    private val network: NetworkModel by optionNetwork()
    private val programAddress: String by optionAddress().required()

    override fun run() {
        val program = program(network, programAddress)
        val ethereumParser = EthereumParser()
        val ethereumInstructions = ethereumParser.parse(program.bytecode.deserializeBytes())
        val ethereumTranspiler = EthereumTranspiler()
        val instructions = ethereumTranspiler.transpile(ethereumInstructions)
        val basicGraph = BASIC_GRAPH_BUILDER.build(instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        logger.log(program, "Graph:")
        basicGraphVisualized.render(System.out)
        System.out.flush()
    }
}
