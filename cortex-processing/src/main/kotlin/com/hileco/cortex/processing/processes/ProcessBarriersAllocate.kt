package com.hileco.cortex.processing.processes

import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.geth.GethLoader
import com.hileco.cortex.processing.processes.Logger.Companion.logger
import java.math.BigDecimal

class ProcessBarriersAllocate {
    private val modelClient = ModelClient()
    private val gethLoader = GethLoader()
    private val ethereumBarriers = EthereumBarriers()

    fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        if (networkModel.name != LOCAL_NETWORK_NAME && networkModel.network != LOCAL_NETWORK) {
            return
        }
        val ethereumBarrierPrograms = ethereumBarriers.all()
        logger.log(networkModel, "Allocating balance to up to ${ethereumBarrierPrograms.size} programs")
        modelClient.programs(0, ethereumBarrierPrograms.size).forEach { programModel ->
            if (programModel.balance == BigDecimal.ZERO) {
                val result = gethLoader.executeGeth("setup-barrier-balance.js", networkModel.networkAddress, programModel.location.programAddress)
                logger.log(programModel, "Allocated balance: $result")
            }
        }
    }

    companion object {
        const val LOCAL_NETWORK_NAME = "Ethereum"
        const val LOCAL_NETWORK = "local"
    }
}
