package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.geth.GethContractLoader
import com.hileco.cortex.processing.histogram.ProgramHistogramBuilder
import java.math.BigDecimal

class ProgramLoaderProcess : BaseProcess() {
    private val gethContractLoader = GethContractLoader()
    private val programHistogramBuilder = ProgramHistogramBuilder()
    private val modelClient = ModelClient()

    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        val scanBlockNumberStart = networkModel.scanningBlock
        val scanBlockNumberLimit = networkModel.latestBlock - MARGIN
        if (scanBlockNumberLimit <= scanBlockNumberStart) {
            return
        }
        val scanBlockNumberEnd = (networkModel.scanningBlock + BLOCKS_PER_SCAN).min(scanBlockNumberLimit)
        val contracts = gethContractLoader.load(networkModel, scanBlockNumberStart, scanBlockNumberEnd)
        contracts.forEach { contract ->
            modelClient.programEnsure(ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = networkModel.name,
                            blockchainNetwork = networkModel.network,
                            blockNumber = scanBlockNumberStart,
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
        modelClient.networkUpdateScannedBlock(networkModel, scanBlockNumberEnd)
    }

    companion object {
        private val BLOCKS_PER_SCAN = BigDecimal.valueOf(20)
        private val MARGIN = BigDecimal(20)
    }
}
