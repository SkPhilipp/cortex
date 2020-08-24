package com.hileco.cortex.console.graphics

import com.googlecode.lanterna.Symbols.*
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.screen.TerminalScreen

class Box(screen: TerminalScreen,
          private val position: TerminalPosition,
          private val size: TerminalSize,
          private val colorScheme: ColorScheme = ColorScheme(
                  foreground = TextColor.ANSI.WHITE,
                  background = TextColor.ANSI.BLACK,
                  foregroundHighlight = TextColor.ANSI.BLACK,
                  backgroundHighlight = TextColor.ANSI.WHITE
          ),
          private val isExtension: Boolean = false) {
    private val textGraphics: TextGraphics = screen.newTextGraphics()

    fun bottom(): Int {
        return position.withRelativeRow(size.rows).row
    }

    fun right(): Int {
        return position.withRelativeColumn(size.columns).column
    }

    fun draw() {
        textGraphics.foregroundColor = colorScheme.foreground
        textGraphics.backgroundColor = colorScheme.background
        textGraphics.fillRectangle(position, size, ' ')
        val topLeft = position
        val topRight = position.withRelativeColumn(size.columns)
        val bottomLeft = position.withRelativeRow(size.rows)
        val bottomRight = position.withRelativeRow(size.rows).withRelativeColumn(size.columns)
        textGraphics.drawLine(topLeft, topRight, DOUBLE_LINE_HORIZONTAL)
        textGraphics.drawLine(topRight, bottomRight, DOUBLE_LINE_VERTICAL)
        textGraphics.drawLine(bottomRight, bottomLeft, DOUBLE_LINE_HORIZONTAL)
        textGraphics.drawLine(bottomLeft, topLeft, DOUBLE_LINE_VERTICAL)
        textGraphics.setCharacter(topLeft, if (!isExtension) DOUBLE_LINE_TOP_LEFT_CORNER else DOUBLE_LINE_T_DOWN)
        textGraphics.setCharacter(topRight, DOUBLE_LINE_TOP_RIGHT_CORNER)
        textGraphics.setCharacter(bottomLeft, if (!isExtension) DOUBLE_LINE_BOTTOM_LEFT_CORNER else DOUBLE_LINE_T_UP)
        textGraphics.setCharacter(bottomRight, DOUBLE_LINE_BOTTOM_RIGHT_CORNER)
    }

    fun title(value: String) {
        text(value, -2)
    }

    fun text(value: String, line: Int = 0, highlight: Boolean = false) {
        textGraphics.foregroundColor = if (highlight) colorScheme.foregroundHighlight else colorScheme.foreground
        textGraphics.backgroundColor = if (highlight) colorScheme.backgroundHighlight else colorScheme.background
        val fitted = when {
            value.length < size.columns - 1 -> value.padEnd(size.columns - 1)
            value.length > size.columns - 1 -> value.substring(0, (size.columns - 1))
            else -> value
        }
        textGraphics.putString(position.withRelative(1, 1 + line), fitted)
    }
}
