package com.hileco.cortex.processing.database

import org.bson.BsonDocument
import org.bson.BsonElement
import org.bson.BsonString

class ModelClient {

    fun networkEnsure(networkModel: NetworkModel) {
        val existing = databaseClient.networks().find(BsonDocument(
                listOf(
                        BsonElement("name", BsonString(networkModel.name)),
                        BsonElement("network", BsonString(networkModel.network))
                )
        ))
        if (existing.asSequence().any()) {
            databaseClient.networks().insertOne(networkModel)
        }
    }

    companion object {
        val databaseClient: DatabaseClient by lazy { DatabaseClient() }
    }
}