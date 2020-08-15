package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.BlockModel
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.geth.GethBlockLoader
import com.hileco.cortex.processing.geth.GethBlockchainLoader
import java.math.BigDecimal

class BlockLoaderProcess : BaseProcess() {
    private val gethBlockLoader = GethBlockLoader()
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
        if (blockModelMostRecent != null && blockModelMostRecent.number.min(networkModel.latestBlock) <= MARGIN) {
            return
        }
        val gethBlock = gethBlockLoader.load(networkModel, networkModel.latestBlock)
        modelClient.blockEnsure(BlockModel(
                blockchainName = networkModel.name,
                blockchainNetwork = networkModel.network,
                number = gethBlock.number
        ))
    }

    companion object {
        private val MARGIN = BigDecimal.valueOf(20)
    }
}