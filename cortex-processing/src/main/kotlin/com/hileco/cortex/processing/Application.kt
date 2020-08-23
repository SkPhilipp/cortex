package com.hileco.cortex.processing

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.processes.*
import com.hileco.cortex.processing.processes.BarrierSetupProcess.Companion.LOCAL_NETWORK
import com.hileco.cortex.processing.processes.BarrierSetupProcess.Companion.LOCAL_NETWORK_NAME
import java.math.BigDecimal

fun main() {
    val modelClient = ModelClient()
    modelClient.networkEnsure(NetworkModel(
            name = LOCAL_NETWORK_NAME,
            network = LOCAL_NETWORK,
            networkAddress = "localhost",
            latestBlock = BigDecimal(0),
            scanningBlock = BigDecimal(0),
            processing = true
    ))
    BlockLoaderProcess().startThread()
    ProgramLoaderProcess().startThread()
    ProgramAnalysisProcess().startThread()
    TransactionSendProcess().startThread()
    BarrierSetupProcess().startThread()
}
