package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.long
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.Network
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.vm.bytes.toBackedInteger
import java.math.BigDecimal

fun RawOption.network() = convert {
    return@convert Network.values().firstOrNull { network -> network.internalName == it } ?: throw BadParameterValue("No network with name '$it'")
}.default(Network.ETHEREUM_PRIVATE)

fun RawOption.backedInteger() = convert {
    return@convert it.toBackedInteger()
}

sealed class SelectionContext(name: String) : OptionGroup(name) {
    abstract fun programs(modelClient: ModelClient): Iterator<ProgramModel>

    abstract fun network(): Network
}

class AddressSelectionContext : SelectionContext("Options for selecting by a single program's address") {
    private val programNetwork by option(help = "Network within which to operate").network()
    private val programAddress by option(help = "Address of the program on which to operate").required()

    override fun programs(modelClient: ModelClient): Iterator<ProgramModel> {
        val program = modelClient.program(programNetwork, programAddress) ?: throw BadParameterValue("No program '${programAddress}'")
        return listOf(program).iterator()
    }

    override fun network(): Network {
        return programNetwork
    }
}

class BlocksSelectionContext : SelectionContext("Options for selecting within a range of blocks") {
    private val blockNetwork by option(help = "Network within which to operate").network()
    val blockStart by option(help = "Blocks within which to operate").long().default(0)
    val blocks by option(help = "Amount of blocks to operate in forwards (positive) or backwards (negative) from the block start").long().default(1)

    override fun programs(modelClient: ModelClient): Iterator<ProgramModel> {
        val start = BigDecimal.valueOf(blockStart)
        val end = BigDecimal.valueOf(blockStart + blocks)
        return modelClient.programs(blockNetwork, start, end)
    }

    override fun network(): Network {
        return blockNetwork
    }
}
