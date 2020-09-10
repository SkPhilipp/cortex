package com.hileco.cortex.processing.processes

import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.geth.GethLoader
import com.hileco.cortex.processing.processes.Logger.Companion.logger

class ProcessBarriersDeploy {
    private val modelClient = ModelClient()
    private val gethLoader = GethLoader()
    private val ethereumBarriers = EthereumBarriers()

    fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        if (networkModel.name != LOCAL_NETWORK_NAME && networkModel.network != LOCAL_NETWORK) {
            return
        }
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val transaction = gethLoader.executeGeth("setup-barrier-deploy.js", networkModel.networkAddress, ethereumBarrier.contractSetupCode)
            logger.log(networkModel, "${ethereumBarrier.id} is created by transaction $transaction")
        }
    }

    companion object {
        const val LOCAL_NETWORK_NAME = "Ethereum"
        const val LOCAL_NETWORK = "local"
    }
}
