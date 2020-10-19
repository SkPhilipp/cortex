package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.defaultByName
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.option
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.Network
import com.hileco.cortex.processing.web3rpc.Web3Client
import java.math.BigInteger

class BarriersAllocateCommand : CliktCommand(name = "barriers-allocate", help = "Assigns a balance to all programs on the ${Network.ETHEREUM_PRIVATE} network") {
    private val selection by option()
            .groupChoice("address" to AddressSelectionContext(), "blocks" to BlocksSelectionContext())
            .defaultByName("blocks")

    override fun run() {
        val selectedNetwork = selection.selectNetwork()
        if (selectedNetwork.name != Network.ETHEREUM_PRIVATE.internalName) {
            throw IllegalStateException("This action is only allowed on programs on the ${Network.ETHEREUM_PRIVATE} network")
        }
        val web3Client = Web3Client()
        val web3ActiveNetworkId = web3Client.loadNetworkId()
        if (web3ActiveNetworkId != selectedNetwork.blockchainId) {
            throw IllegalStateException("Web3 client is not running against the ${Network.ETHEREUM_PRIVATE} network, instead has network id of $web3ActiveNetworkId")
        }
        val modelClient = ModelClient()
        val programSelection = selection.selectPrograms(modelClient)
        programSelection.forEach { program ->
            logger.log(selectedNetwork, "Sending wei to ${program.location.programAddress}")
            web3Client.sendWei(program.location.programAddress, BigInteger.valueOf(1000))
        }
    }
}
