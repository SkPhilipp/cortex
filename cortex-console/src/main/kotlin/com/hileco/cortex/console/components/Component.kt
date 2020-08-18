package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.input.KeyStroke

interface Component {
    val position: TerminalPosition
    val bottom: Int
    val right: Int
    fun draw()
    fun handleKeyStroke(keyStroke: KeyStroke) {
    }
}
