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
                .sort(Document("number", 1))
                .limit(1)
                .firstOrNull()
    }

    /**
     * Updates a given [NetworkModel].
     */
    fun networkUpdate(model: NetworkModel) {
        databaseClient.networks().updateOne(
                Document(mapOf(
                        "name" to model.name,
                        "network" to model.network
                )),
                Document("\$set", Document("latestBlock", model.latestBlock))
        )
    }

    companion object {
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}