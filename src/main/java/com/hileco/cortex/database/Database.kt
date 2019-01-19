package com.hileco.cortex.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients

class Database {
    companion object {
        val programRepository: ProgramRepository

        init {
            val mongoClient: MongoClient = MongoClients.create(System.getenv("CORTEX_MONGODB"))
            val database = mongoClient.getDatabase("cortex")
            programRepository = ProgramRepository(database.getCollection("programs"))
        }
    }
}
