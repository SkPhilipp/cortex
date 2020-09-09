package com.hileco.cortex.processing

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.hileco.cortex.processing.processes.*

class MainCommand : CliktCommand(help = "CORTEX MAIN CONSOLE") {
    override fun run() {
    }
}

class AnalyzeExploreCommand : CliktCommand(name = "analyze", help = "Analyze the next available program") {
    override fun run() {
        ProcessAnalyzeExplore().run()
    }
}

class DeployBarriersCommand : CliktCommand(name = "deploy-barriers", help = "Deploys barrier programs") {
    override fun run() {
        ProcessDeployBarriers().run()
    }
}

class ReportCommand : CliktCommand(name = "report", help = "Print the analysis reports for a program") {
    private val programAddress: String by argument(help = "Address of the program to report on")

    override fun run() {
        ProcessReport().run(programAddress)
    }
}

class SearchCommand : CliktCommand(name = "search", help = "Searches the active blockchain for programs") {
    private val start: Int by option(help = "Starting block").int().default(0)
    private val limit: Int by option(help = "Limit of blocks to inspect forwards (positive) or backwards (negative) from the starting block").int().default(-1000)
    private val margin: Int by option(help = "Distance to keep from the most recent block").int().default(20)

    override fun run() {
        ProcessSearch().run(start, limit, margin)
    }
}

class SetupCommand : CliktCommand(name = "setup", help = "Initializes the local network") {
    override fun run() {
        ProcessSetup().run()
    }
}

fun main(argv: Array<String>) {
    MainCommand().subcommands(
            SearchCommand(),
            AnalyzeExploreCommand(),
            ReportCommand(),
            DeployBarriersCommand(),
            SetupCommand()
    ).main(argv)
}
