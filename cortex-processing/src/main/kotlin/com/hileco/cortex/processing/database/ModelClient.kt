package com.hileco.cortex.processing.database

import org.bson.Document

class ModelClient {

    /**
     * Ensures the given [NetworkModel] exists.
     */
    fun networkEnsure(model: NetworkModel) {
        val existing = databaseClient.networks().find(Document(mapOf(
                "name" to model.name,
                "network" to model.network
        )))
        if (existing.any()) {
            return
        }
        databaseClient.networks().insertOne(model)
    }

    /**
     * Retrieves a [NetworkModel] where processing is true.
     */
    fun networkProcessing(): NetworkModel? {
        return databaseClient.networks().find(Document("processing", true)).firstOrNull()
    }

    /**
     * Ensures the given [BlockModel] exists.
     */
    fun blockEnsure(model: BlockModel) {
        val existing = databaseClient.blocks().find(Document(mapOf(
                "blockchainName" to model.blockchainName,
                "blockchainNetwork" to model.blockchainNetwork,
                "number" to model.number
        )))
        if (existing.any()) {
            return
        }
        databaseClient.blocks().insertOne(model)
    }

    /**
     * Retrieves the most recent [BlockModel] for a network by its number.
     */
    fun blockMostRecent(model: NetworkModel): BlockModel? {
        return databaseClient.blocks().find(
                Document(mapOf(
                        "blockchainName" to model.name,
                        "blockchainNetwork" to model.network
                )))
                .sort(Document("number", -1))
                .limit(1)
                .firstOrNull()
    }

    /**
     * Updates a given [NetworkModel].
     */
    fun networkUpdate(model: NetworkModel) {
        databaseClient.networks().updateMany(
                Document(mapOf(
                        "name" to model.name,
                        "network" to model.network
                )),
                Document("\$set", Document("latestBlock", model.latestBlock))
        )
    }

    fun blockLeastRecentUnloaded(model: NetworkModel): BlockModel? {
        return databaseClient.blocks().find(
                Document(mapOf(
                        "blockchainName" to model.name,
                        "blockchainNetwork" to model.network,
                        "loaded" to false
                )))
                .sort(Document("number", 1))
                .limit(1)
                .firstOrNull()
    }

    fun programEnsure(model: ProgramModel) {
        val existing = databaseClient.programs().find(Document(mapOf(
                "location.blockchainName" to model.location.blockchainName,
                "location.blockchainNetwork" to model.location.blockchainNetwork,
                "location.blockNumber" to model.location.blockNumber,
                "location.transactionHash" to model.location.transactionHash
        )))
        if (existing.any()) {
            return
        }
        databaseClient.programs().insertOne(model)
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

    fun blockUpdate(model: BlockModel) {
        databaseClient.blocks().updateMany(
                Document(mapOf(
                        "blockchainName" to model.blockchainName,
                        "blockchainNetwork" to model.blockchainNetwork,
                        "number" to model.number
                )),
                Document("\$set", Document("loaded", model.loaded))
        )
    }

    companion object {
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}
