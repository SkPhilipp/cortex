package com.hileco.cortex.processing.commands

import com.hileco.cortex.processing.database.Network
import com.hileco.cortex.processing.database.ProgramModel

class Logger {
    fun log(text: String) {
        println("[---] [---] $text")
    }

    fun log(network: Network, text: String) {
        println("[${network.internalName}] [---] $text")
    }

    fun log(programModel: ProgramModel, text: String) {
        println("[${programModel.location.networkName}] [${programModel.location.programAddress}] $text")
    }

    companion object {
        val logger = Logger()
    }
}
