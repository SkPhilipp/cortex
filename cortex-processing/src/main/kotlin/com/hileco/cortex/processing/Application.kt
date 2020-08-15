package com.hileco.cortex.processing

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.processes.BlockLoaderProcess
import com.hileco.cortex.processing.processes.ProgramAnalysisProcess
import com.hileco.cortex.processing.processes.ProgramLoaderProcess
import com.hileco.cortex.processing.processes.TransactionSendProcess
import java.math.BigDecimal

fun main() {
    val modelClient = ModelClient()
    modelClient.networkEnsure(NetworkModel(
            name = "Ethereum",
            network = "local",
            networkAddress = "localhost",
            latestBlock = BigDecimal(0),
            processing = true
    ))
    val blockLoaderProcess = BlockLoaderProcess()
    blockLoaderProcess.startThread()
    val programAnalysisProcess = ProgramAnalysisProcess()
    programAnalysisProcess.startThread()
    val programLoaderProcess = ProgramLoaderProcess()
    programLoaderProcess.startThread()
    val transactionSendProcess = TransactionSendProcess()
    transactionSendProcess.startThread()
}
