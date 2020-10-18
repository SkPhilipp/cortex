package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.BadParameterValue
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.long
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.vm.bytes.toBackedInteger
import java.math.BigDecimal

fun RawOption.network() = convert {
    val modelClient = ModelClient()
    return@convert modelClient.networkByNetwork(it) ?: throw BadParameterValue("No network '$it'")
}.defaultLazy {
    val modelClient = ModelClient()
    modelClient.networkProcessing() ?: throw BadParameterValue("No default network")
}

fun RawOption.backedInteger() = convert {
    return@convert it.toBackedInteger()
}

sealed class SelectionContext(name: String) : OptionGroup(name) {
    abstract fun selectPrograms(modelClient: ModelClient): Iterator<ProgramModel>
}

class AddressSelectionContext : SelectionContext("Options for selecting by a single program's address") {
    val programNetwork by option(help = "Network within which to operate").network()
    val programAddress by option(help = "Address of the program on which to operate").required()

    override fun selectPrograms(modelClient: ModelClient): Iterator<ProgramModel> {
        val program = modelClient.program(programNetwork, programAddress) ?: throw BadParameterValue("No program '${programAddress}'")
        return listOf(program).iterator()
    }
}

class BlocksSelectionContext : SelectionContext("Options for selecting within a range of blocks") {
    val blocksNetwork by option(help = "Network within which to operate").network()
    val blockStart by option(help = "Blocks within which to operate").long().default(0)
    val blocks by option(help = "Amount of blocks to operate in forwards (positive) or backwards (negative) from the block start").long().default(1)

    override fun selectPrograms(modelClient: ModelClient): Iterator<ProgramModel> {
        val start = BigDecimal.valueOf(blockStart)
        val end = BigDecimal.valueOf(blockStart + blocks)
        return modelClient.programs(blocksNetwork, start, end)
    }
}
