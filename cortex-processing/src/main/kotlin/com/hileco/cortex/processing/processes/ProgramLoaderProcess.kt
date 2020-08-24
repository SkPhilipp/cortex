package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.fingerprint.ProgramHistogramBuilder
import com.hileco.cortex.processing.fingerprint.ProgramIdentifier
import com.hileco.cortex.processing.geth.GethContractLoader
import java.math.BigDecimal

class ProgramLoaderProcess : BaseProcess() {
    private val gethContractLoader = GethContractLoader()
    private val programHistogramBuilder = ProgramHistogramBuilder()
    private val programIdentifier = ProgramIdentifier()
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
            val histogram = programHistogramBuilder.histogram(contract.bytecode)
            modelClient.programEnsure(ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = networkModel.name,
                            blockchainNetwork = networkModel.network,
                            blockNumber = scanBlockNumberStart,
                            transactionHash = contract.transactionHash,
                            programAddress = contract.address
                    ),
                    bytecode = contract.bytecode,
                    histogram = histogram,
                    identifiedAs = programIdentifier.identify(histogram),
                    disk = mapOf(),
                    balance = contract.balance,
                    analyses = mutableListOf()
            ))
        }
        modelClient.networkUpdateScannedBlock(networkModel, scanBlockNumberEnd + BigDecimal.ONE)
    }

    companion object {
        private val BLOCKS_PER_SCAN = BigDecimal.valueOf(100)
        private val MARGIN = BigDecimal(20)
    }
}
