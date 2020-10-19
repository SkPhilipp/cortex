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
                "name" to model.name
        )),
                Document("\$setOnInsert", databaseClient.asDocument(model)),
                UpdateOptions().upsert(true))
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
                        "name" to model.name
                )),
                Document("\$set", Document("latestBlock", latestBlock))
        )
    }

    /**
     * Updates a given [NetworkModel].
     */
    fun networkUpdateScanningBlock(model: NetworkModel, scanningBlock: BigDecimal) {
        databaseClient.networks().updateMany(
                Document(mapOf(
                        "name" to model.name
                )),
                Document("\$set", Document("scanningBlock", scanningBlock))
        )
    }

    fun programEnsure(model: ProgramModel) {
        databaseClient.programs().updateOne(Document(mapOf(
                "location.networkName" to model.location.networkName,
                "location.blockNumber" to model.location.blockNumber,
                "location.transactionHash" to model.location.transactionHash
        )),
                Document("\$setOnInsert", databaseClient.asDocument(model)),
                UpdateOptions().upsert(true))
    }

    fun programUpdate(model: ProgramModel) {
        databaseClient.programs().updateMany(
                Document(mapOf(
                        "location.networkName" to model.location.networkName,
                        "location.blockNumber" to model.location.blockNumber,
                        "location.transactionHash" to model.location.transactionHash

                )),
                Document("\$set", Document("analyses", model.analyses))
        )
    }

    fun program(network: NetworkModel, programAddress: String): ProgramModel? {
        return databaseClient.programs().find(
                Document(mapOf(
                        "location.networkName" to network.name,
                        "location.programAddress" to programAddress
                )))
                .firstOrNull()
    }

    fun programs(network: NetworkModel, blockStart: BigDecimal, blockEnd: BigDecimal): Iterator<ProgramModel> {
        return databaseClient.programs().find(
                Document(mapOf(
                        "location.networkName" to network.name,
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

    fun setup() {
        databaseClient.setup()
        Network.values().forEach {
            networkEnsure(NetworkModel(
                    name = it.internalName,
                    blockchain = it.blockchain,
                    blockchainId = it.blockchainId,
                    latestBlock = BigDecimal(0),
                    scanningBlock = BigDecimal(0)
            ))
        }
    }

    companion object {
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}
