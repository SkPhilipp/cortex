package com.hileco.cortex.processing.database

import com.hileco.cortex.processing.processes.Logger
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.BsonDocument
import org.bson.BsonDocumentWrapper
import org.bson.Document
import org.bson.codecs.BigDecimalCodec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider


class DatabaseClient {
    private val database: MongoDatabase

    init {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN")
        val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(BigDecimalCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(
                        PojoCodecProvider.builder()
                                .automatic(true)
                                .build()
                )
        )
        val connectionString = ConnectionString("mongodb+srv://$USERNAME:$PASSWORD@$HOST/$DATABASE_NAME?retryWrites=true&w=majority")
        val settings: MongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build()
        val client = MongoClients.create(settings)
        database = client.getDatabase(DATABASE_NAME)
    }

    fun networks(): MongoCollection<NetworkModel> {
        return database.getCollection("network", NetworkModel::class.java)
    }

    fun programs(): MongoCollection<ProgramModel> {
        return database.getCollection("program", ProgramModel::class.java)
    }

    fun transactions(): MongoCollection<TransactionModel> {
        return database.getCollection("transaction", TransactionModel::class.java)
    }

    fun asDocument(any: Any): BsonDocument {
        return BsonDocumentWrapper.asBsonDocument(any, database.codecRegistry)
    }

    fun reset() {
        Logger.logger.log("Clearing all collections in 5 seconds")
        Thread.sleep(5000)
        Logger.logger.log("Clearing all networks")
        networks().deleteMany(Document())
        Logger.logger.log("Clearing all programs")
        programs().deleteMany(Document())
        Logger.logger.log("Clearing all transactions")
        transactions().deleteMany(Document())
    }
    fun setup() {
        Logger.logger.log("Creating collections")
        listOf("network", "program", "transaction").minus(database.listCollectionNames()).forEach {
            Logger.logger.log("Creating collection $it")
            database.createCollection(it)
        }
        Logger.logger.log("Creating network index")
        networks().createIndex(Document(mapOf(
                "name" to 1,
                "network" to 1
        )))
        Logger.logger.log("Creating network index")
        networks().createIndex(Document(mapOf(
                "processing" to 1
        )))
        Logger.logger.log("Creating programs index")
        programs().createIndex(Document(mapOf(
                "location.blockchainName" to 1,
                "location.blockchainNetwork" to 1,
                "location.blockNumber" to 1,
                "location.transactionHash" to 1
        )))
        Logger.logger.log("Creating programs index")
        programs().createIndex(Document(mapOf(
                "location.blockchainName" to 1,
                "location.blockchainNetwork" to 1,
                "location.blockNumber" to 1,
                "analyses" to 1
        )))
        Logger.logger.log("Creating programs index")
        programs().createIndex(Document(mapOf(
                "histogram" to 1
        )))
    }

    companion object {
        private val USERNAME = System.getProperty("CORTEX_DB_USERNAME", "pepe")
        private val PASSWORD = System.getProperty("CORTEX_DB_PASSWORD", "pZKUxMw7QoBSomCpppePeTheFroggo")
        private val HOST = System.getProperty("CORTEX_DB_HOST", "cortex-000.zules.mongodb.net")
        private val DATABASE_NAME = System.getProperty("CORTEX_DB_NAME", "cortex")
    }
}
