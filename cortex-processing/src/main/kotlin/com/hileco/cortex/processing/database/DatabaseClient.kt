package com.hileco.cortex.processing.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.codecs.BigDecimalCodec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.slf4j.impl.SimpleLogger


class DatabaseClient {

    private val database: MongoDatabase

    init {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
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
        listOf("network", "block", "program", "transaction").minus(database.listCollectionNames()).forEach {
            database.createCollection(it)
        }
    }

    fun networks(): MongoCollection<NetworkModel> {
        return database.getCollection("network", NetworkModel::class.java)
    }

    fun blocks(): MongoCollection<BlockModel> {
        return database.getCollection("block", BlockModel::class.java)
    }

    fun programs(): MongoCollection<ProgramModel> {
        return database.getCollection("program", ProgramModel::class.java)
    }

    fun transactions(): MongoCollection<TransactionModel> {
        return database.getCollection("transaction", TransactionModel::class.java)
    }

    companion object {
        private const val USERNAME = "pepe"
        private const val PASSWORD = "VZFtAMMIY7hKOfro"
        private const val HOST = "cortex-000.zules.mongodb.net"
        private const val DATABASE_NAME = "cortex"
    }
}