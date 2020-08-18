package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.console.graphics.Box
import com.hileco.cortex.console.views.InstructionsView
import com.hileco.cortex.console.views.SymbolicPathEntryView
import com.hileco.cortex.console.views.SymbolicProgramStoreZoneView
import com.hileco.cortex.console.views.SymbolicStackView
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.symbolic.explore.SymbolicProgramDebugger
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramStoreZone

fun debugger(): SymbolicProgramDebugger {
    val ethereumBarriers = EthereumBarriers()
    val ethereumBarrier1 = ethereumBarriers.all().first()
    val program = SymbolicProgram(ethereumBarrier1.cortexInstructions)
    val programContext = SymbolicProgramContext(program)
    val virtualMachine = SymbolicVirtualMachine(programContext)
    return SymbolicProgramDebugger(virtualMachine)
}

fun main() {
    val defaultTerminalFactory = DefaultTerminalFactory()
    defaultTerminalFactory.setInitialTerminalSize(TerminalSize(140, 45))
    defaultTerminalFactory.setTerminalEmulatorTitle("Cortex Console // Symbolic Virtual Machine Debugger")
    val terminal: Terminal = defaultTerminalFactory.createTerminal()
    val screen = TerminalScreen(terminal)
    screen.startScreen()
    screen.cursorPosition = null

    val background = Background(screen)
    background.draw()

    var debugger = debugger()

    val instructionsView = InstructionsView(screen, TerminalPosition(1, 2), 23)
    val stackView = SymbolicStackView(screen, TerminalPosition(instructionsView.right() + 2, 2), 10)
    val memoryView = SymbolicProgramStoreZoneView(ProgramStoreZone.MEMORY, screen, TerminalPosition(stackView.right() + 2, 2), 10);
    val callDataView = SymbolicProgramStoreZoneView(ProgramStoreZone.CALL_DATA, screen, TerminalPosition(instructionsView.right() + 2, stackView.bottom() + 3), 10);
    val diskView = SymbolicProgramStoreZoneView(ProgramStoreZone.DISK, screen, TerminalPosition(callDataView.right() + 2, memoryView.bottom() + 3), 10);
    val exitView = Box(screen, TerminalPosition(1, instructionsView.bottom() + 3), TerminalSize(27, 3))
    val pathView = SymbolicPathEntryView(screen, TerminalPosition(exitView.right() + 2, instructionsView.bottom() + 3), 10)

    var redraw = true
    loop@ while (true) {
        instructionsView.draw()
        val instructions = debugger.virtualMachine.programs.first().program.instructions
        val positionedInstructions = debugger.virtualMachine.programs.first().program.instructionsAbsolute
        val instructionPosition = debugger.virtualMachine.programs.first().instructionPosition
        val positionedInstruction = positionedInstructions[instructionPosition] ?: throw IllegalStateException()
        if (redraw) {
            instructionsView.drawContent(instructions, positionedInstruction.relativePosition)
            stackView.draw()
            stackView.drawContent(debugger.virtualMachine.programs.first().stack)
            memoryView.draw()
            memoryView.drawContent(debugger.virtualMachine.programs.first().memory)
            callDataView.draw()
            callDataView.drawContent(debugger.virtualMachine.programs.first().callData)
            diskView.draw()
            diskView.drawContent(debugger.virtualMachine.programs.first().program.storage)
            exitView.title(value = "state")
            exitView.draw()
            pathView.draw()
            pathView.drawContent(debugger.virtualMachine.path)
            val exitedReason = debugger.virtualMachine.exitedReason
            if (exitedReason != null) {
                exitView.text(exitedReason.name)
            }
            screen.refresh()
            redraw = false
        }
        Thread.sleep(10)
        val keyStroke: KeyStroke? = screen.pollInput()
        if (keyStroke != null) {
            when {
                keyStroke.keyType == KeyType.EOF || keyStroke.keyType == KeyType.Escape -> {
                    break@loop
                }
                keyStroke.keyType == KeyType.ArrowRight -> {
                    debugger.stepTake()
                    redraw = true
                }
                keyStroke.keyType == KeyType.ArrowDown -> {
                    debugger.stepSkip()
                    redraw = true
                }
                keyStroke.character == 'r' -> {
                    debugger = debugger()
                    redraw = true
                }
            }
        }
        Thread.yield()
    }
    screen.close()
}
