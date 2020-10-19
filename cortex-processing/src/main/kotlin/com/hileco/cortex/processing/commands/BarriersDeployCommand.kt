package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.Network
import com.hileco.cortex.processing.web3rpc.Web3Client

class BarriersDeployCommand : CliktCommand(name = "barriers-deploy", help = "Deploys barrier programs on the on the ${Network.ETHEREUM_PRIVATE} network") {
    private val network by option(help = "Network within which to operate").network()

    override fun run() {
        if (network != Network.ETHEREUM_PRIVATE) {
            throw IllegalStateException("This action is only allowed on programs on the ${Network.ETHEREUM_PRIVATE} network")
        }
        val web3Client = Web3Client(network.defaultEndpoint)
        val web3ActiveNetworkId = web3Client.loadNetworkId()
        if (web3ActiveNetworkId != Network.ETHEREUM_PRIVATE.blockchainId) {
            throw IllegalStateException("Web3 client is not running against the ${Network.ETHEREUM_PRIVATE} network, instead has network id of $web3ActiveNetworkId")
        }
        val ethereumBarriers = EthereumBarriers()
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val transactionHash = web3Client.createContract(ethereumBarrier.contractSetupCode)
            logger.log(network, "${ethereumBarrier.id} was created by transaction $transactionHash")
        }
    }
}
