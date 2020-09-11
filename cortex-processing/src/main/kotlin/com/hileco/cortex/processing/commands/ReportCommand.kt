package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.NetworkModel

class ReportCommand : CliktCommand(name = "report", help = "Print the analysis reports for a program") {
    private val network: NetworkModel by optionNetwork()
    private val programAddress: String by optionAddress().required()

    override fun run() {
        val program = program(network, programAddress)
        logger.log(program, "Reports:")
        program.analyses.forEach { report ->
            logger.log(program, report.toString())
        }
    }
}
