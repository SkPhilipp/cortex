package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.console.views.SymbolicProgramDebuggerView
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import kotlin.system.exitProcess

private fun buildScreen(): TerminalScreen {
    val defaultTerminalFactory = DefaultTerminalFactory()
    defaultTerminalFactory.setInitialTerminalSize(TerminalSize(140, 45))
    defaultTerminalFactory.setTerminalEmulatorTitle("Cortex Console // Symbolic Virtual Machine Debugger")
    val terminal: Terminal = defaultTerminalFactory.createTerminal()
    val screen = TerminalScreen(terminal)
    screen.startScreen()
    screen.cursorPosition = null
    return screen
}

fun main() {
    val screen = buildScreen()
    val view = SymbolicProgramDebuggerView(screen, TerminalPosition(1, 2)) {
        val ethereumBarriers = EthereumBarriers()
        val program = SymbolicProgram(ethereumBarriers.all().first().cortexInstructions)
        val programContext = SymbolicProgramContext(program)
        SymbolicVirtualMachine(programContext)
    }
    var redraw = true
    while (true) {
        if (redraw) {
            view.draw()
            screen.refresh()
            redraw = false
        }
        Thread.sleep(10)
        val keyStroke: KeyStroke? = screen.pollInput()
        if (keyStroke != null) {
            try {
                if (keyStroke.keyType == KeyType.EOF || keyStroke.keyType == KeyType.Escape) {
                    exitProcess(0)
                } else {
                    view.handleKeyStroke(keyStroke)
                    redraw = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
