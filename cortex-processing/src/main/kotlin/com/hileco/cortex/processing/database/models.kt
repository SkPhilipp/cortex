package com.hileco.cortex.processing.database

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.math.BigDecimal

data class NetworkModel @BsonCreator constructor(
        @BsonProperty("name") val name: String,
        @BsonProperty("network") val network: String,
        @BsonProperty("networkAddress") val networkAddress: String,
        @BsonProperty("latestBlock") val latestBlock: BigDecimal
)

data class BlockModel @BsonCreator constructor(
        @BsonProperty("blockchainName") val blockchainName: String,
        @BsonProperty("number") val number: BigDecimal
)

data class ProgramModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel,
        @BsonProperty("bytecode") val bytecode: String,
        @BsonProperty("histogram") val histogram: String,
        @BsonProperty("disk") val disk: Map<String, String>,
        @BsonProperty("currency") val currency: BigDecimal,
        @BsonProperty("analyses") val analyses: List<AnalysisReportModel>
)

data class AnalysisReportModel @BsonCreator constructor(
        @BsonProperty("callSolved") val callSolved: Boolean,
        @BsonProperty("callSolution") val callSolution: String,
        @BsonProperty("callTargetAddress") val callTargetAddress: String
)

data class TransactionModel @BsonCreator constructor(
        @BsonProperty("location") val location: TransactionLocationModel
)

data class TransactionLocationModel @BsonCreator constructor(
        @BsonProperty("blockchainName") val blockchainName: String,
        @BsonProperty("blockchainNetwork") val blockchainNetwork: String,
        @BsonProperty("blockNumber") val blockNumber: BigDecimal,
        @BsonProperty("transactionHash") val transactionHash: String,
        @BsonProperty("programAddress") val programAddress: String
)
