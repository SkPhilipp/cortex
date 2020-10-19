package com.hileco.cortex.processing.commands

import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel

class Logger {
    fun log(text: String) {
        println("[---:---] $text")
    }

    fun log(networkModel: NetworkModel, text: String) {
        println("[${networkModel.name}] $text")
    }

    fun log(programModel: ProgramModel, text: String) {
        println("[${programModel.location.networkName}] [${programModel.location.programAddress}] $text")
    }

    companion object {
        val logger = Logger()
    }
}
