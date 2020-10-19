package com.hileco.cortex.processing.database

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class NetworkModel @BsonCreator constructor(
        @BsonProperty("name") val name: String,
        @BsonProperty("blockchain") val blockchain: String,
        @BsonProperty("blockchainId") val blockchainId: String,
        @BsonProperty("latestBlock") var latestBlock: BigDecimal,
        @BsonProperty("scanningBlock") var scanningBlock: BigDecimal,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class ProgramModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel,
        @BsonProperty("bytecode") val bytecode: String,
        @BsonProperty("histogram") val histogram: String,
        @BsonProperty("identifiedAs") val identifiedAs: String,
        @BsonProperty("disk") val disk: Map<String, String>,
        @BsonProperty("balance") val balance: BigDecimal,
        @BsonProperty("analyses") val analyses: MutableList<AnalysisReportModel>,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class AnalysisReportModel @BsonCreator constructor(
        @BsonProperty("type") val type: String,
        @BsonProperty("completed") val completed: Boolean,
        @BsonProperty("solution") val solution: String? = null,
        @BsonProperty("solvable") val solvable: Boolean? = null,
        @BsonProperty("errorCause") val errorCause: String? = null
)

data class TransactionModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel,
        @BsonProperty("createdTime") val createdTime: LocalDateTime = LocalDateTime.now()
)

data class TransactionLocationModel @BsonCreator constructor(
        @BsonProperty("networkName") val networkName: String,
        @BsonProperty("blockNumber") val blockNumber: BigDecimal,
        @BsonProperty("transactionHash") val transactionHash: String,
        @BsonProperty("programAddress") val programAddress: String
)
