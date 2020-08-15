package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

class GethBlockchainLoader {

    /**
     * TODO: Replace mock implementation
     */
    fun load(networkModel: NetworkModel): GethBlockchainState {
        val latestBlock = (networkModel.latestBlock + BigDecimal.ONE).min(BigDecimal.valueOf(250))
        return GethBlockchainState(latestBlock)
    }
}