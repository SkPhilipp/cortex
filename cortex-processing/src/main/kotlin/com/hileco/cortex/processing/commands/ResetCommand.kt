package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hileco.cortex.processing.database.ModelClient


class ResetCommand : CliktCommand(name = "reset", help = "Removes all database state & performs database setup") {
    override fun run() {
        ModelClient.databaseClient.reset()
        val modelClient = ModelClient()
        modelClient.setup()
    }
}
