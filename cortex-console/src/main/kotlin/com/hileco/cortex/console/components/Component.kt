package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition

interface Component {
    val position: TerminalPosition
    val bottom: Int
    val right: Int
    fun draw()
}
