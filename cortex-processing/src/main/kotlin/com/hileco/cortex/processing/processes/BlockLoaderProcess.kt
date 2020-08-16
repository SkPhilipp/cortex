package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.geth.GethBlockchainLoader

class BlockLoaderProcess : BaseProcess() {
    private val gethBlockchainLoader = GethBlockchainLoader()
    private val modelClient = ModelClient()

    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        val gethBlockchainState = gethBlockchainLoader.load(networkModel)
        modelClient.networkUpdateLatestBlock(networkModel, gethBlockchainState.latestBlock)
    }
}