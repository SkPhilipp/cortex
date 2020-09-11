package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.database.TransactionLocationModel
import com.hileco.cortex.processing.fingerprint.ProgramHistogramBuilder
import com.hileco.cortex.processing.fingerprint.ProgramIdentifier
import com.hileco.cortex.processing.geth.GethBlockchainLoader
import com.hileco.cortex.processing.geth.GethContractLoader

class SearchCommand : CliktCommand(name = "search", help = "Searches the active blockchain for programs") {
    private val network: NetworkModel by optionNetwork()
    private val start: Int by argument(help = "Starting block").int()
    private val limit: Int by option(help = "Limit of blocks to inspect forwards (positive) or backwards (negative) from the starting block").int().default(100)
    private val margin: Int by option(help = "Distance to keep from the most recent block").int().default(10)

    override fun run() {
        val gethBlockchainLoader = GethBlockchainLoader()
        val gethBlockchainState = gethBlockchainLoader.load(network)
        val modelClient = ModelClient()
        modelClient.networkUpdateLatestBlock(network, gethBlockchainState.latestBlock)
        logger.log(network, "latest block is ${gethBlockchainState.latestBlock}")

        val latestBlock = gethBlockchainState.latestBlock
        val effectiveStart = start.coerceAtMost(latestBlock.toInt() - margin)
        val effectiveEnd = (margin + limit).coerceAtMost(latestBlock.toInt() - margin)
        logger.log(network, "searching $effectiveStart through $effectiveEnd")

        val gethContractLoader = GethContractLoader()
        val contracts = gethContractLoader.load(network, effectiveStart, effectiveEnd)
        contracts.forEach { contract ->
            val programHistogramBuilder = ProgramHistogramBuilder()
            val programHistogram = programHistogramBuilder.histogram(contract.bytecode)
            val programIdentifier = ProgramIdentifier()
            val programIdentity = programIdentifier.identify(programHistogram)
            val programModel = ProgramModel(
                    location = TransactionLocationModel(
                            blockchainName = network.name,
                            blockchainNetwork = network.network,
                            // TODO: This is not correct, it should be the block number of the contract
                            blockNumber = start.toBigDecimal(),
                            transactionHash = contract.transactionHash,
                            programAddress = contract.address
                    ),
                    bytecode = contract.bytecode,
                    histogram = programHistogram,
                    identifiedAs = programIdentity,
                    disk = mapOf(),
                    balance = contract.balance,
                    analyses = mutableListOf()
            )
            modelClient.programEnsure(programModel)
            logger.log(programModel, "found (identified as '${programModel.identifiedAs}')")
        }
    }
}
