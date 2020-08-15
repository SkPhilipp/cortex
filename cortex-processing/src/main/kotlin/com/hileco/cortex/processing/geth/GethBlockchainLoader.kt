package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel

class GethBlockchainLoader : GethLoader() {
    private val loadBlockchainScript by lazy { unpackage("geth-scripts/load-blockchain.js") }

    fun load(networkModel: NetworkModel): GethBlockchainState {
        val result = executeGeth(loadBlockchainScript, mapOf(
                "networkAddress" to networkModel.networkAddress
        ))
        return GethBlockchainState(
                latestBlock = result.get("latestBlock").decimalValue()
        )
    }
}
