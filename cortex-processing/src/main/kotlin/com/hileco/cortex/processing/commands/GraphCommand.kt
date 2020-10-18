package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.defaultByName
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.option
import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient

class GraphCommand : CliktCommand(name = "graph", help = "Print the analysis graph for a program") {
    private val selection by option()
            .groupChoice("address" to AddressSelectionContext(), "blocks" to BlocksSelectionContext())
            .defaultByName("address")

    override fun run() {
        val modelClient = ModelClient()
        val programSelection = selection.selectPrograms(modelClient)
        programSelection.forEachRemaining { program ->
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
}
