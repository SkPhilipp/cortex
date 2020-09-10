package com.hileco.cortex.processing.processes

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.processes.Logger.Companion.logger

class ProcessGraph {
    private val modelClient = ModelClient()

    fun run(programAddress: String) {
        val networkModel = modelClient.networkProcessing() ?: return
        val programModel = modelClient.program(networkModel, programAddress)
        if (programModel == null) {
            logger.log(networkModel, "No program with address $programAddress")
            return
        }
        val ethereumParser = EthereumParser()
        val ethereumInstructions = ethereumParser.parse(programModel.bytecode.deserializeBytes())
        val ethereumTranspiler = EthereumTranspiler()
        val instructions = ethereumTranspiler.transpile(ethereumInstructions)
        val basicGraph = BASIC_GRAPH_BUILDER.build(instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        logger.log(programModel, "Graph:")
        basicGraphVisualized.render(System.out)
        System.out.flush()
    }
}
