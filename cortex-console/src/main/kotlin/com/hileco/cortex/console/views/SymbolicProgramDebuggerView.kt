package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.components.DelegatingComponent
import com.hileco.cortex.console.compositions.SymbolicProgramComposition
import com.hileco.cortex.symbolic.explore.SymbolicProgramDebugger
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine

class SymbolicProgramDebuggerView(screen: TerminalScreen,
                                  initialPosition: TerminalPosition,
                                  private val symbolicVirtualMachineSupplier: () -> SymbolicVirtualMachine) :
        DelegatingComponent<SymbolicProgramComposition>(SymbolicProgramComposition(screen, symbolicVirtualMachineSupplier(), initialPosition)) {
    private var symbolicProgramDebugger = SymbolicProgramDebugger(delegate.virtualMachine)

    override fun draw() {
        delegate.draw()
    }

    override fun handleKeyStroke(keyStroke: KeyStroke) {
        when {
            keyStroke.keyType == KeyType.ArrowRight -> {
                symbolicProgramDebugger.stepTake()
            }
            keyStroke.keyType == KeyType.ArrowDown -> {
                symbolicProgramDebugger.stepSkip()
            }
            keyStroke.character == 'r' -> {
                val symbolicVirtualMachine = symbolicVirtualMachineSupplier()
                symbolicProgramDebugger = SymbolicProgramDebugger(symbolicVirtualMachine)
                delegate.virtualMachine = symbolicVirtualMachine
            }
        }
    }
}