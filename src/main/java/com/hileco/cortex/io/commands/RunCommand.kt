package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.io.serialization.InstructionParser
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import java.io.File
import java.io.InputStream

class RunCommand : CliktCommand(name = "run", help = "Generate input for a given sample which conforms with given constraints") {
    private val source: File? by option(help = "Cortex Assembly source file path, defaults to stdin").file()

    override fun run() {
        execute(source?.inputStream() ?: System.`in`)
    }

    fun execute(instructionStream: InputStream) {
        val instructionParser = InstructionParser()
        val instructions = instructionStream.reader().readLines().map { instructionParser.parse(it) }
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
    }
}
