package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal


class ProcessSetup {
    fun run() {
        ModelClient.databaseClient.setup()
        val modelClient = ModelClient()
        val networkModel = NetworkModel(
                name = ProcessBarriersDeploy.LOCAL_NETWORK_NAME,
                network = ProcessBarriersDeploy.LOCAL_NETWORK,
                networkAddress = "localhost",
                latestBlock = BigDecimal(0),
                scanningBlock = BigDecimal(0),
                processing = true
        )
        Logger.logger.log(networkModel, "Initializing")
        modelClient.networkEnsure(networkModel)
    }
}
