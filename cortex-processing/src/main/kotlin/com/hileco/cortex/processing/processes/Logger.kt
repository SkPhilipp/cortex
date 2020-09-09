package com.hileco.cortex.processing.processes

import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel

class Logger {
    fun log(text: String) {
        println("[---:---] $text")
    }

    fun log(networkModel: NetworkModel, text: String) {
        println("[${networkModel.name}:${networkModel.network}] $text")
    }

    fun log(programModel: ProgramModel, text: String) {
        println("[${programModel.location.blockchainName}:${programModel.location.blockchainNetwork}] [${programModel.location.programAddress}] $text")
    }

    companion object {
        val logger = Logger()
    }
}
