package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.fingerprint.ProgramHistogramBuilder
import com.hileco.cortex.processing.fingerprint.ProgramIdentifier
import com.hileco.cortex.processing.geth.GethBlockchainLoader
import com.hileco.cortex.processing.geth.GethContractLoader
import com.hileco.cortex.processing.processes.Logger.Companion.logger

class ProcessSearch {
    private val gethContractLoader = GethContractLoader()
    private val gethBlockchainLoader = GethBlockchainLoader()
    private val programHistogramBuilder = ProgramHistogramBuilder()
    private val programIdentifier = ProgramIdentifier()
    private val modelClient = ModelClient()

    fun run(blockNumberStart: Int,
            blockNumberLimit: Int,
            blockNumberMargin: Int) {
        val networkModel = modelClient.networkProcessing() ?: return
        val gethBlockchainState = gethBlockchainLoader.load(networkModel)
        logger.log(networkModel, "latest block is $gethBlockchainState.latestBlock")
        modelClient.networkUpdateLatestBlock(networkModel, gethBlockchainState.latestBlock)
        val latestBlock = gethBlockchainState.latestBlock
        val effectiveStart = blockNumberStart.coerceAtMost(latestBlock.toInt() - blockNumberMargin)
        val effectiveEnd = (blockNumberStart + blockNumberLimit).coerceAtMost(latestBlock.toInt() - blockNumberMargin)
        val contracts = gethContractLoader.load(networkModel, effectiveStart, effectiveEnd)
        contracts.forEach { contract ->
            val histogram = programHistogramBuilder.histogram(contract.bytecode)
            val programModel = ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = networkModel.name,
                            blockchainNetwork = networkModel.network,
                            // TODO: This is not correct, it should be the block number of the contract
                            blockNumber = blockNumberStart.toBigDecimal(),
                            transactionHash = contract.transactionHash,
                            programAddress = contract.address
                    ),
                    bytecode = contract.bytecode,
                    histogram = histogram,
                    identifiedAs = programIdentifier.identify(histogram),
                    disk = mapOf(),
                    balance = contract.balance,
                    analyses = mutableListOf()
            )
            modelClient.programEnsure(programModel)
            logger.log(programModel, "found")
        }
    }
}
