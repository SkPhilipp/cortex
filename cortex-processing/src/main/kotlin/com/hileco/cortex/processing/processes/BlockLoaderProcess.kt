package com.hileco.cortex.processing.processes

class BlockLoaderProcess : BaseProcess() {
    override fun run() {
        println("Would load in some blocks and update the blockchain state using GethBlockchainLoader and GethBlockLoader")
    }
}