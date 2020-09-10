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

class BarriersAllocateCommand : CliktCommand(name = "barriers-allocate", help = "Assigns a balance to barrier programs") {
    override fun run() {
        ProcessBarriersAllocate().run()
    }
}

class BarriersDeployCommand : CliktCommand(name = "barriers-deploy", help = "Deploys barrier programs") {
    override fun run() {
        ProcessBarriersDeploy().run()
    }
}

class GraphCommand : CliktCommand(name = "graph", help = "Print the analysis graph for a program") {
    private val programAddress: String by argument(help = "Address of the program to report on")

    override fun run() {
        ProcessGraph().run(programAddress)
    }
}

class ReportCommand : CliktCommand(name = "report", help = "Print the analysis reports for a program") {
    private val programAddress: String by argument(help = "Address of the program to report on")

    override fun run() {
        ProcessReport().run(programAddress)
    }
}

class ResetCommand : CliktCommand(name = "reset", help = "Removes all database state & performs database setup") {
    override fun run() {
        ProcessReset().run()
        ProcessSetup().run()
    }
}

class SearchCommand : CliktCommand(name = "search", help = "Searches the active blockchain for programs") {
    private val start: Int by argument(help = "Starting block").int()
    private val limit: Int by option(help = "Limit of blocks to inspect forwards (positive) or backwards (negative) from the starting block").int().default(100)
    private val margin: Int by option(help = "Distance to keep from the most recent block").int().default(10)

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
            AnalyzeExploreCommand(),
            BarriersAllocateCommand(),
            BarriersDeployCommand(),
            GraphCommand(),
            ReportCommand(),
            ResetCommand(),
            SearchCommand(),
            SetupCommand()
    ).main(argv)
}
