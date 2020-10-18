package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.web3rpc.Web3Client

class BarriersDeployCommand : CliktCommand(name = "barriers-deploy", help = "Deploys barrier programs") {
    override fun run() {
        val ethereumBarriers = EthereumBarriers()
        val web3Client = Web3Client()
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val transactionHash = web3Client.createContract(ethereumBarrier.contractSetupCode)
            logger.log("${ethereumBarrier.id} was created by transaction $transactionHash")
        }
    }
}
