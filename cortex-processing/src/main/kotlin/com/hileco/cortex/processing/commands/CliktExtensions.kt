package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.vm.bytes.toBackedInteger

fun RawOption.network() = convert {
    val modelClient = ModelClient()
    return@convert modelClient.networkByNetwork(it) ?: throw BadParameterValue("No network '$it'")
}

fun RawOption.backedInteger() = convert {
    return@convert it.toBackedInteger()
}

fun CliktCommand.optionNetwork(): OptionWithValues<NetworkModel, NetworkModel, NetworkModel> {
    return this.option(help = "Network within which to operate").network().defaultLazy {
        val modelClient = ModelClient()
        modelClient.networkProcessing() ?: throw BadParameterValue("No default network")
    }
}

fun CliktCommand.optionAddress(): RawOption {
    return this.option(help = "Address of the program on which to operate")
}

fun CliktCommand.program(networkModel: NetworkModel, address: String): ProgramModel {
    val modelClient = ModelClient()
    return modelClient.program(networkModel, address) ?: throw BadParameterValue("No program '$address'")
}
