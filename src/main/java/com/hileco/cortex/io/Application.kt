package com.hileco.cortex.io

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.hileco.cortex.io.commands.AttackCommand
import com.hileco.cortex.io.commands.OptmizeCommand
import com.hileco.cortex.io.commands.RunCommand

class Application : CliktCommand() {
    override fun run() = Unit
}

fun main(args: Array<String>) = Application()
        .subcommands(OptmizeCommand(), AttackCommand(), RunCommand())
        .main(args)
