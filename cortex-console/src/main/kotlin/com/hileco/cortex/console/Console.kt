package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.console.components.Component
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.console.views.ProgramSelectionView
import com.hileco.cortex.console.views.SymbolicProgramDebuggerView
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.ethereum.EthereumTranspiler
import com.hileco.cortex.ethereum.deserializeBytes
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import kotlin.system.exitProcess

class Console(val screen: TerminalScreen) {
    private var background = Background(screen)
    private lateinit var view: Component

    fun programSelection() {
        this.background.draw()
        this.view = ProgramSelectionView(screen, TerminalPosition(1, 2)) {
            symbolicProgramDebugger(it)
        }
    }

    private fun symbolicProgramDebugger(programModel: ProgramModel) {
        this.background.draw()
        this.view = SymbolicProgramDebuggerView(screen, TerminalPosition(1, 2)) {
            val ethereumParser = EthereumParser()
            val ethereumInstructions = ethereumParser.parse(programModel.bytecode.deserializeBytes())
            val ethereumTranspiler = EthereumTranspiler()
            val instructions = ethereumTranspiler.transpile(ethereumInstructions)
            val program = SymbolicProgram(instructions)
            val programContext = SymbolicProgramContext(program)
            SymbolicVirtualMachine(programContext)
        }
    }

    fun refresh() {
        this.view.draw()
        this.screen.refresh()
    }

    fun checkInput(): Boolean {
        val keyStroke: KeyStroke = screen.pollInput() ?: return false
        try {
            if (keyStroke.keyType == KeyType.EOF || keyStroke.keyType == KeyType.Escape) {
                if (view is SymbolicProgramDebuggerView) {
                    programSelection()
                } else if (view is ProgramSelectionView) {
                    exitProcess(0)
                }
            } else {
                view.handleKeyStroke(keyStroke)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    companion object {
        fun build(): Console {
            val defaultTerminalFactory = DefaultTerminalFactory()
            defaultTerminalFactory.setInitialTerminalSize(TerminalSize(140, 45))
            defaultTerminalFactory.setTerminalEmulatorTitle("Cortex Console // Symbolic Virtual Machine Debugger")
            val terminal: Terminal = defaultTerminalFactory.createTerminal()
            val screen = TerminalScreen(terminal)
            screen.startScreen()
            screen.cursorPosition = null
            val console = Console(screen)
            console.programSelection()
            return console
        }
    }
}
