package com.hileco.cortex.processing.database

import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import java.math.BigDecimal

class ModelClient {
    /**
     * Ensures the given [NetworkModel] exists.
     */
    fun networkEnsure(model: NetworkModel) {
        databaseClient.networks().updateOne(Document(mapOf(
                "name" to model.name,
                "network" to model.network
        )),
                Document("\$setOnInsert", databaseClient.asDocument(model)),
                UpdateOptions().upsert(true))
    }

    /**
     * Retrieves a [NetworkModel] where processing is true.
     */
    fun networkProcessing(): NetworkModel? {
        return databaseClient.networks().find(Document("processing", true)).firstOrNull()
    }

    /**
     * Retrieves a [NetworkModel] by network and networkIdentifier.
     */
    fun networkByNetworkAndNetworkIdentifier(network: String, networkIdentifier: String): NetworkModel? {
        return databaseClient.networks().find(Document(mapOf(
                "network" to network,
                "networkIdentifier" to networkIdentifier
        ))).firstOrNull()
    }

    /**
     * Retrieves a [NetworkModel] by name.
     */
    fun networkByName(name: String): NetworkModel? {
        return databaseClient.networks().find(Document(mapOf(
                "name" to name
        ))).firstOrNull()
    }

    /**
     * Updates a given [NetworkModel].
     */
    fun networkUpdateLatestBlock(model: NetworkModel, latestBlock: BigDecimal) {
        databaseClient.networks().updateMany(
                Document(mapOf(
                        "name" to model.name,
                        "network" to model.network
                )),
                Document("\$set", Document("latestBlock", latestBlock))
        )
    }

    /**
     * Updates a given [NetworkModel].
     */
    fun networkUpdateScannedBlock(model: NetworkModel, scanningBlock: BigDecimal) {
        databaseClient.networks().updateMany(
                Document(mapOf(
                        "name" to model.name,
                        "network" to model.network
                )),
                Document("\$set", Document("scanningBlock", scanningBlock))
        )
    }

    fun programEnsure(model: ProgramModel) {
        databaseClient.programs().updateOne(Document(mapOf(
                "location.blockchainName" to model.location.blockchainName,
                "location.blockchainNetwork" to model.location.blockchainNetwork,
                "location.blockNumber" to model.location.blockNumber,
                "location.transactionHash" to model.location.transactionHash
        )),
                Document("\$setOnInsert", databaseClient.asDocument(model)),
                UpdateOptions().upsert(true))
    }

    fun programUpdate(model: ProgramModel) {
        databaseClient.programs().updateMany(
                Document(mapOf(
                        "location.blockchainName" to model.location.blockchainName,
                        "location.blockchainNetwork" to model.location.blockchainNetwork,
                        "location.blockNumber" to model.location.blockNumber,
                        "location.transactionHash" to model.location.transactionHash

                )),
                Document("\$set", Document("analyses", model.analyses))
        )
    }

    fun program(network: NetworkModel, programAddress: String): ProgramModel? {
        return databaseClient.programs().find(
                Document(mapOf(
                        "location.blockchainName" to network.name,
                        "location.blockchainNetwork" to network.network,
                        "location.programAddress" to programAddress
                )))
                .firstOrNull()
    }

    fun programs(network: NetworkModel, blockStart: BigDecimal, blockEnd: BigDecimal): Iterator<ProgramModel> {
        return databaseClient.programs().find(
                Document(mapOf(
                        "location.blockchainName" to network.name,
                        "location.blockchainNetwork" to network.network,
                        "location.blockNumber" to Document("\$gte", blockStart),
                        "location.blockNumber" to Document("\$lte", blockEnd)
                )))
                .iterator()
    }

    fun programs(skip: Int, limit: Int): Iterable<ProgramModel> {
        return databaseClient.programs().find()
                .sort(Document("location.blockNumber", 1))
                .skip(skip)
                .limit(limit)
    }

    private fun setupNetwork(name: String, network: String, networkAddress: String, processing: Boolean = false) {
        networkEnsure(NetworkModel(
                name = name,
                network = network,
                networkIdentifier = networkAddress,
                latestBlock = BigDecimal(0),
                scanningBlock = BigDecimal(0),
                processing = processing
        ))
    }

    fun setup() {
        databaseClient.setup()
        setupNetwork("mainnet", ETHEREUM, "1")
        setupNetwork("morden", ETHEREUM, "2")
        setupNetwork("ropsten", ETHEREUM, "3")
        setupNetwork("rinkeby", ETHEREUM, "4")
        setupNetwork("goerli", ETHEREUM, "5")
        setupNetwork("morden", ETHEREUM, "42")
        setupNetwork("classic", ETHEREUM, "61")
        setupNetwork(ETHEREUM_PRIVATE_NETWORK, ETHEREUM, "1337", processing = true)
    }

    companion object {
        const val ETHEREUM = "Ethereum"
        const val ETHEREUM_PRIVATE_NETWORK = "private"
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}
