package com.hileco.cortex.console.graphics

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen

class Table(screen: TerminalScreen,
            val position: TerminalPosition,
            val height: Int,
            columnWidths: List<Int>,
            private val colorScheme: ColorScheme = ColorScheme(
                    foreground = TextColor.ANSI.WHITE,
                    background = TextColor.ANSI.BLACK,
                    foregroundHighlight = TextColor.ANSI.BLACK,
                    backgroundHighlight = TextColor.ANSI.WHITE
            )) {
    private val columnBoxes: List<Box>

    fun bottom(): Int {
        return columnBoxes.lastOrNull()?.bottom() ?: position.row
    }

    fun right(): Int {
        return columnBoxes.lastOrNull()?.right() ?: position.column
    }

    init {
        var currentWidth = 0
        var currentIsExtension = false
        columnBoxes = columnWidths.map { columnWidth ->
            val box = Box(
                    screen = screen,
                    position = position.withRelativeColumn(currentWidth),
                    size = TerminalSize(columnWidth, height),
                    colorScheme = colorScheme,
                    isExtension = currentIsExtension
            )
            currentWidth += columnWidth
            currentIsExtension = true
            box
        }
                .toList()
    }

    fun draw() {
        columnBoxes.forEach {
            it.draw()
        }
    }

    fun title(value: String, column: Int) {
        columnBoxes[column].title(value)
    }

    fun textTable(values: List<List<String>>) {
        val limit = values.size.coerceAtMost(height - 1)
        for (i in 0 until limit) {
            textRow(values[i], i)
        }
    }

    fun textRow(values: List<String>, line: Int = 0, highlight: Boolean = false) {
        val limit = values.size.coerceAtMost(columnBoxes.size)
        for (i in 0 until limit) {
            textCell(values[i], i, line, highlight)
        }
    }

    fun textCell(value: String, column: Int, line: Int = 0, highlight: Boolean = false) {
        columnBoxes[column].text(value, line, highlight)
    }
}
