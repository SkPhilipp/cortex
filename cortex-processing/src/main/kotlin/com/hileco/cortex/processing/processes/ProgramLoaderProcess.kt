package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.geth.GethContractLoader
import com.hileco.cortex.processing.histogram.ProgramHistogramBuilder

class ProgramLoaderProcess : BaseProcess() {
    private val gethContractLoader = GethContractLoader()
    private val programHistogramBuilder = ProgramHistogramBuilder()
    private val modelClient = ModelClient()

    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        val blockModel = modelClient.blockLeastRecentUnloaded(networkModel) ?: return
        val contracts = gethContractLoader.load(networkModel, blockModel)
        contracts.forEach { contract ->
            modelClient.programEnsure(ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = networkModel.name,
                            blockchainNetwork = networkModel.network,
                            blockNumber = blockModel.number,
                            transactionHash = contract.transactionHash,
                            programAddress = contract.address
                    ),
                    bytecode = contract.bytecode,
                    histogram = programHistogramBuilder.histogram(contract.bytecode),
                    disk = mapOf(),
                    currency = contract.currency,
                    analyses = mutableListOf()
            ))
        }
        blockModel.loaded = true
        modelClient.blockUpdate(blockModel)
    }
}
