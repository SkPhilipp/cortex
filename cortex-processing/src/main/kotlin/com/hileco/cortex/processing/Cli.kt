package com.hileco.cortex.processing

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.hileco.cortex.processing.commands.*
import kotlin.system.exitProcess

class MainCommand : CliktCommand(help = "CORTEX MAIN CONSOLE") {
    override fun run() {
    }
}

fun main(argv: Array<String>) {
    MainCommand().subcommands(
            AnalyzeCommand(),
            BarriersAllocateCommand(),
            BarriersDeployCommand(),
            GraphCommand(),
            ReportCommand(),
            ResetCommand(),
            SearchCommand(),
            SetupCommand()
    ).main(argv)
    exitProcess(0)
}
