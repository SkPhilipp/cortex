package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ModelClient.Companion.LOCAL_NETWORK
import com.hileco.cortex.processing.web3rpc.Web3Client
import java.math.BigDecimal
import java.math.BigInteger

class BarriersAllocateCommand : CliktCommand(name = "barriers-allocate", help = "Assigns a balance to barrier programs") {

    override fun run() {
        val modelClient = ModelClient()
        val ethereumBarriers = EthereumBarriers()
        val web3Client = Web3Client()
        val ethereumBarrierPrograms = ethereumBarriers.all()
        logger.log("Allocating balance to up to ${ethereumBarrierPrograms.size} programs")
        modelClient.programs(0, ethereumBarrierPrograms.size)
                .filter { it.location.blockchainNetwork == LOCAL_NETWORK }
                .forEach { programModel ->
                    if (programModel.balance == BigDecimal.ZERO && programModel.identifiedAs.startsWith("barrier")) {
                        web3Client.sendWei(programModel.location.programAddress, BigInteger.valueOf(1000))
                        logger.log(programModel, "Allocated balance to ${programModel.location.programAddress}")
                    }
                }
    }
}
