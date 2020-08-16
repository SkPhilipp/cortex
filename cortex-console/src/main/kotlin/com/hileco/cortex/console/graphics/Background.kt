package com.hileco.cortex.console.graphics

import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen

class Background(private val screen: TerminalScreen) {
    fun draw() {
        for (row in 0 until screen.terminalSize.rows) {
            for (column in 0 until screen.terminalSize.columns) {
                screen.setCharacter(column, row, TextCharacter(' ', TextColor.ANSI.DEFAULT, TextColor.ANSI.BLACK))
            }
        }
    }
}
