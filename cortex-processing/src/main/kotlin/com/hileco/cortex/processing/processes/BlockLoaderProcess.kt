package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.BlockModel
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.geth.GethBlockchainLoader
import java.math.BigDecimal

class BlockLoaderProcess : BaseProcess() {
    private val gethBlockchainLoader = GethBlockchainLoader()
    private val modelClient = ModelClient()

    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        val gethBlockchainState = gethBlockchainLoader.load(networkModel)
        networkModel.latestBlock = gethBlockchainState.latestBlock
        modelClient.networkUpdate(networkModel)
        if (networkModel.latestBlock == BigDecimal.valueOf(0)) {
            return
        }
        val blockModelMostRecent = modelClient.blockMostRecent(networkModel)
        if (blockModelMostRecent != null && networkModel.latestBlock - blockModelMostRecent.number <= MARGIN) {
            return
        }
        val nextBlockNumber = if (blockModelMostRecent != null) blockModelMostRecent.number + BigDecimal.ONE else BigDecimal.ZERO
        val limitBlockNumber = (gethBlockchainState.latestBlock - MARGIN).max(BigDecimal.ZERO)
        val lastBlockNumber = (nextBlockNumber + BigDecimal.valueOf(100)).min(limitBlockNumber)
        for (blockNumber in nextBlockNumber.toInt()..lastBlockNumber.toInt()) {
            modelClient.blockEnsure(BlockModel(
                    blockchainName = networkModel.name,
                    blockchainNetwork = networkModel.network,
                    number = BigDecimal(blockNumber),
                    loaded = false
            ))
        }
    }

    companion object {
        private val MARGIN = BigDecimal.valueOf(20)
    }
}