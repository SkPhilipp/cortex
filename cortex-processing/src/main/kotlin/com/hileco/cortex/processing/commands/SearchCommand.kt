package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.fingerprint.ProgramHistogramBuilder
import com.hileco.cortex.processing.fingerprint.ProgramIdentifier
import com.hileco.cortex.processing.web3rpc.Web3Client

class SearchCommand : CliktCommand(name = "search", help = "Searches the active blockchain for programs") {
    private val network: NetworkModel by optionNetwork()
    private val start: Long by argument(help = "Starting block").long()
    private val limit: Long by option(help = "Limit of blocks to inspect forwards (positive) or backwards (negative) from the starting block").long().default(100)
    private val margin: Long by option(help = "Distance to keep from the most recent block").long().default(10)

    override fun run() {
        val web3Client = Web3Client()
        val latestBlockNumber = web3Client.loadBlockNumber()
        val modelClient = ModelClient()
        modelClient.networkUpdateLatestBlock(network, latestBlockNumber.toBigDecimal())
        logger.log(network, "latest block is $latestBlockNumber")

        val effectiveStart = start.coerceAtMost(latestBlockNumber.toLong() - margin)
        val effectiveEnd = (margin + limit).coerceAtMost(latestBlockNumber.toLong() - margin)
        logger.log(network, "searching $effectiveStart through $effectiveEnd")

        val contracts = web3Client.loadContracts(effectiveStart, effectiveEnd)
        contracts.forEach { contract ->
            val programHistogramBuilder = ProgramHistogramBuilder()
            val programHistogram = programHistogramBuilder.histogram(contract.bytecode)
            val programIdentifier = ProgramIdentifier()
            val programIdentity = programIdentifier.identify(programHistogram)
            val programModel = ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = network.name,
                            blockchainNetwork = network.network,
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
            logger.log(programModel, "found (identified as '${programModel.identifiedAs}')")
        }
    }
}
