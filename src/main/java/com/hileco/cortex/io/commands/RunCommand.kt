package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.io.serialization.InstructionParser
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes
import java.io.File
import java.io.InputStream

class RunCommand : CliktCommand(name = "run", help = "Generate input for a given sample which conforms with given constraints") {
    private val source: File by option(help = "Cortex Assembly source file path").file().required()
    private val callData: LayeredBytes by option(help = "Cortex Assembly source file path").convert { LayeredBytesReader().read(it) }.default(LayeredBytes())

    override fun run() {
        execute(source.inputStream(), callData)
    }

    fun execute(instructionStream: InputStream, callData: LayeredBytes) {
        val instructionParser = InstructionParser()
        val instructions = instructionStream.reader().readLines().map { instructionParser.parse(it) }
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        programContext.callData.write(0, callData.bytes)
        val programRunner = ProgramRunner(processContext)
        try {
            programRunner.run()
        } catch (e: ProgramException) {
            echo("Halted at instruction position ${e.programContext.instructionPosition}: ${e.reason}", err = true)
        }
    }
}
