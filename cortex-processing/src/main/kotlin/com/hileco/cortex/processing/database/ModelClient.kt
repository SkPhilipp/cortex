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

    fun programLeastRecentUnanalyzed(model: NetworkModel): ProgramModel? {
        return databaseClient.programs().find(
                Document(mapOf(
                        "location.blockchainName" to model.name,
                        "location.blockchainNetwork" to model.network,
                        "analyses" to listOf<AnalysisReportModel>()
                )))
                .sort(Document("location.blockNumber", 1))
                .limit(1)
                .firstOrNull()
    }

    fun programs(skip: Int, limit: Int): Iterable<ProgramModel> {
        return databaseClient.programs().find()
                .sort(Document("location.blockNumber", 1))
                .skip(skip)
                .limit(limit)
    }

    companion object {
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}
