package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ModelClient.Companion.LOCAL_NETWORK
import com.hileco.cortex.processing.geth.GethLoader
import java.math.BigDecimal

class BarriersAllocateCommand : CliktCommand(name = "barriers-allocate", help = "Assigns a balance to barrier programs") {

    override fun run() {
        val modelClient = ModelClient()
        val gethLoader = GethLoader()
        val ethereumBarriers = EthereumBarriers()
        val networkModel = modelClient.networkByNetwork(LOCAL_NETWORK)
                ?: throw IllegalStateException("Barrier allocation requires local network")
        val ethereumBarrierPrograms = ethereumBarriers.all()
        logger.log(networkModel, "Allocating balance to up to ${ethereumBarrierPrograms.size} programs")
        modelClient.programs(0, ethereumBarrierPrograms.size)
                .filter { it.location.blockchainNetwork == LOCAL_NETWORK }
                .forEach { programModel ->
                    if (programModel.balance == BigDecimal.ZERO) {
                        val result = gethLoader.executeGeth("setup-barrier-balance.js", networkModel.networkAddress, programModel.location.programAddress)
                        logger.log(programModel, "Allocated balance: $result")
                    }
                }
    }
}
