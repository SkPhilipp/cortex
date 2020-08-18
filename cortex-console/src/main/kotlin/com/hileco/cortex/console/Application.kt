package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.console.compositions.SymbolicProgramComposition
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.symbolic.explore.SymbolicProgramDebugger
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine

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

    val composition = SymbolicProgramComposition(screen, debugger.virtualMachine, TerminalPosition(1, 2))

    var redraw = true
    loop@ while (true) {
        if (redraw) {
            composition.draw()
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
                    composition.virtualMachine = debugger.virtualMachine
                    redraw = true
                }
            }
        }
        Thread.yield()
    }
    screen.close()
}
