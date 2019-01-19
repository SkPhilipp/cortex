package com.hileco.cortex.io.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.io.serialization.ProgramReferenceLoader
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredBytes

class RunCommand : CliktCommand(name = "run", help = "Generate input for a given sample which conforms with given constraints") {
    private val program: Program by option(help = "Cortex Assembly source file path").convert { ProgramReferenceLoader().load(it) }.required()
    private val callData: LayeredBytes by option(help = "Call data").convert { LayeredBytesReader().read(it) }.default(LayeredBytes())

    override fun run() {
        execute(program, callData)
    }

    fun execute(program: Program, callData: LayeredBytes) {
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        programContext.callData.write(0, callData.read(0, callData.size))
        val programRunner = ProgramRunner(virtualMachine)
        try {
            programRunner.run()
        } catch (e: ProgramException) {
            echo("Halted at instruction position ${e.programContext.instructionPosition}: ${e.reason}", err = true)
        }
    }
}
