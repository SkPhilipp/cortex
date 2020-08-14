package com.hileco.cortex.processing

import com.hileco.cortex.processing.database.DatabaseClient
import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

fun main() {
    val databaseClient = DatabaseClient()
    databaseClient.networks().insertOne(NetworkModel(
            name = "Ethereum",
            network = "local",
            networkAddress = "localhost",
            latestBlock = BigDecimal(0)
    ))
    databaseClient.networks().find().forEach { println(it) }
}