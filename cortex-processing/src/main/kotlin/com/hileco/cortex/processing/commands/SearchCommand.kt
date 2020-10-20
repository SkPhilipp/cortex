package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.histogram.ProgramHistogramBuilder
import com.hileco.cortex.processing.histogram.ProgramIdentifier
import com.hileco.cortex.processing.web3rpc.Web3Client

class SearchCommand : CliktCommand(name = "search", help = "Searches the active blockchain for programs") {
    private val selection by BlocksSelectionContext()
    private val margin by option(help = "Distance to keep from the most recent block").long().default(10)
    private val threads by option(help = "Amount of search threads").int().default(8)

    override fun run() {
        val network = selection.network()
        val web3Client = Web3Client(network.defaultEndpoint)
        val latestBlockNumber = web3Client.loadBlockNumber()
        val modelClient = ModelClient()
        modelClient.networkUpdateLatestBlock(network, latestBlockNumber.toBigDecimal())
        logger.log(network, "latest block is $latestBlockNumber")
        val effectiveStart = selection.blockStart.coerceAtMost(latestBlockNumber.toLong() - margin)
        val effectiveEnd = (selection.blockStart + selection.blocks).coerceAtMost(latestBlockNumber.toLong() - margin)
        logger.log(network, "searching $effectiveStart through $effectiveEnd")
        val parallelTask = web3Client.loadContracts(effectiveStart, effectiveEnd, threads) { contract ->
            val programHistogramBuilder = ProgramHistogramBuilder()
            val programHistogram = programHistogramBuilder.histogram(contract.bytecode)
            val programIdentifier = ProgramIdentifier()
            val programIdentity = programIdentifier.identify(programHistogram)
            val programModel = ProgramModel(
                    location = TransactionLocationModel(
                            networkName = network.internalName,
                            blockNumber = contract.blockNumberCreated.toBigDecimal(),
                            transactionHash = contract.transactionHash,
                            programAddress = contract.address
                    ),
                    bytecode = contract.bytecode,
                    histogram = programHistogram,
                    identifiedAs = programIdentity,
                    disk = mapOf(),
                    balance = contract.balance.toBigDecimal(),
                    analyses = mutableListOf()
            )
            modelClient.programEnsure(programModel)
            logger.log(programModel, "found a program (identified as '${programModel.identifiedAs}')")
        }
        while (!parallelTask.isComplete()) {
            logger.log(network, "highest searched block is ${parallelTask.resumeFrom()}")
            Thread.sleep(1000)
        }
    }
}
