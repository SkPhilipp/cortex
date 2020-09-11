package com.hileco.cortex.processing.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.hileco.cortex.processing.database.ModelClient

class SetupCommand : CliktCommand(name = "setup", help = "Initializes the local network") {
    override fun run() {
        val modelClient = ModelClient()
        modelClient.setup()
    }
}
