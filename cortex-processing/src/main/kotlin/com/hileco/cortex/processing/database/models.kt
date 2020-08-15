package com.hileco.cortex.processing.database

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class NetworkModel @BsonCreator constructor(
        @BsonProperty("name") val name: String,
        @BsonProperty("network") val network: String,
        @BsonProperty("networkAddress") val networkAddress: String,
        @BsonProperty("latestBlock") var latestBlock: BigDecimal,
        @BsonProperty("processing") var processing: Boolean,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class BlockModel @BsonCreator constructor(
        @BsonProperty("blockchainName") val blockchainName: String,
        @BsonProperty("blockchainNetwork") val blockchainNetwork: String,
        @BsonProperty("number") val number: BigDecimal,
        @BsonProperty("loaded") var loaded: Boolean,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class ProgramModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel,
        @BsonProperty("bytecode") val bytecode: String,
        @BsonProperty("histogram") val histogram: String,
        @BsonProperty("disk") val disk: Map<String, String>,
        @BsonProperty("currency") val currency: BigDecimal,
        @BsonProperty("analyses") val analyses: MutableList<AnalysisReportModel>,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class AnalysisReportModel @BsonCreator constructor(
        @BsonProperty("type") val type: String,
        @BsonProperty("completed") val completed: Boolean,
        @BsonProperty("solution") val solution: String? = null,
        @BsonProperty("solvable") val solvable: Boolean? = null,
        @BsonProperty("errorStage") val errorStage: String? = null,
        @BsonProperty("errorCause") val errorCause: String? = null
)

data class TransactionModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class TransactionLocationModel @BsonCreator constructor(
        @BsonProperty("blockchainName") val blockchainName: String,
        @BsonProperty("blockchainNetwork") val blockchainNetwork: String,
        @BsonProperty("blockNumber") val blockNumber: BigDecimal,
        @BsonProperty("transactionHash") val transactionHash: String,
        @BsonProperty("programAddress") val programAddress: String
)