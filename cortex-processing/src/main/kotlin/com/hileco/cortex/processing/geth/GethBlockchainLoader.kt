package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel

class GethBlockchainLoader : GethLoader() {
    fun load(networkModel: NetworkModel): GethBlockchainState {
        val result = executeGeth("load-blockchain.js", networkModel.networkAddress)
        return GethBlockchainState(
                latestBlock = result.get("latestBlock").decimalValue()
        )
    }
}
