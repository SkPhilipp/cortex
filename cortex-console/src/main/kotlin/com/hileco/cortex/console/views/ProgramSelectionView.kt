package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.components.DelegatingComponent
import com.hileco.cortex.console.components.ProgramModelTable
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.ProgramModel

class ProgramSelectionView(screen: TerminalScreen,
                           initialPosition: TerminalPosition,
                           private val onSelect: (ProgramModel) -> Unit) :
        DelegatingComponent<ProgramModelTable>(ProgramModelTable(screen, initialPosition, 20)) {
    private val modelClient = ModelClient()
    private var programs = listOf<ProgramModel>()

    init {
        programs = modelClient.programs(0, 20).toList()
        delegate.content(programs, 0)
    }

    override fun draw() {
        delegate.draw()
    }

    override fun handleKeyStroke(keyStroke: KeyStroke) {
        when (keyStroke.keyType) {
            KeyType.ArrowUp -> {
                if (delegate.delegate.focusLine > 0) {
                    delegate.delegate.focusLine--
                }
            }
            KeyType.ArrowDown -> {
                delegate.delegate.focusLine++
            }
            KeyType.Enter -> {
                if (delegate.delegate.focusLine < programs.size) {
                    val programModel = programs[delegate.delegate.focusLine]
                    onSelect(programModel)
                }
            }
            else -> {
            }
        }
    }
}
