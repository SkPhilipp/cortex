package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.defaultByName
import com.github.ajalt.clikt.parameters.groups.groupChoice
import com.github.ajalt.clikt.parameters.options.option
import com.hileco.cortex.processing.commands.Logger.Companion.logger
import com.hileco.cortex.processing.database.ModelClient

class ReportCommand : CliktCommand(name = "report", help = "Print the analysis reports for a program") {
    private val selection by option()
            .groupChoice("address" to AddressSelectionContext(), "blocks" to BlocksSelectionContext())
            .defaultByName("address")

    override fun run() {
        val modelClient = ModelClient()
        val programSelection = selection.programs(modelClient)
        programSelection.forEachRemaining { program ->
            if (program.analyses.isEmpty()) {
                logger.log(program, "Has no analysis reports")
            } else {
                logger.log(program, "Analysis reports:")
                program.analyses.forEach { report ->
                    logger.log(program, report.toString())
                }
            }
        }
    }
}
