package com.hileco.cortex.processing.processes

import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.geth.GethLoader
import com.hileco.cortex.processing.processes.Logger.Companion.logger
import java.math.BigDecimal

class ProcessDeployBarriers {
    private val modelClient = ModelClient()
    private val gethLoader = GethLoader()
    private val ethereumBarriers = EthereumBarriers()

    fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        if (networkModel.name != LOCAL_NETWORK_NAME && networkModel.network != LOCAL_NETWORK) {
            return
        }
        barriersDeploy(networkModel)
        barriersAllocateBalance(networkModel)
    }

    private fun barriersDeploy(networkModel: NetworkModel) {
        val ethereumBarrierPrograms = ethereumBarriers.all()
        if (modelClient.programs(0, ethereumBarrierPrograms.size).toList().size >= ethereumBarrierPrograms.size) {
            return
        }
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val result = gethLoader.executeGeth("setup-barrier-deploy.js", networkModel.networkAddress, ethereumBarrier.contractSetupCode)
            logger.log(networkModel, "Deployment of ${ethereumBarrier.id}: $result")
        }
    }

    private fun barriersAllocateBalance(networkModel: NetworkModel) {
        val ethereumBarrierPrograms = ethereumBarriers.all()
        var programs = listOf<ProgramModel>()
        while (programs.size < ethereumBarrierPrograms.size) {
            programs = modelClient.programs(0, ethereumBarrierPrograms.size).toList()
            logger.log(networkModel, "Not allocating until barriers are loaded")
            Thread.sleep(1000)
        }
        programs.forEach { programModel ->
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