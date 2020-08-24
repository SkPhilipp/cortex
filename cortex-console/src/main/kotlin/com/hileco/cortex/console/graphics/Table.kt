package com.hileco.cortex.console.graphics

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.components.Component

class Table(private val screen: TerminalScreen,
            override val position: TerminalPosition,
            private val height: Int,
            private val columns: List<TableColumn>,
            private val dataProvider: (Int) -> List<TableCell>,
            var focusLine: Int = -1,
            private val colorScheme: ColorScheme = ColorScheme(
                    foreground = TextColor.ANSI.WHITE,
                    background = TextColor.ANSI.BLACK,
                    foregroundHighlight = TextColor.ANSI.BLACK,
                    backgroundHighlight = TextColor.ANSI.WHITE
            )) : Component {
    private val columnBoxes: List<Box>

    override val bottom: Int
        get() {
            return columnBoxes.lastOrNull()?.bottom() ?: position.row
        }

    override val right: Int
        get() {
            return columnBoxes.lastOrNull()?.right() ?: position.column
        }

    init {
        var currentWidth = 0
        var currentIsExtension = false
        columnBoxes = columns.map { column ->
            val box = Box(
                    screen = screen,
                    position = position.withRelativeColumn(currentWidth),
                    size = TerminalSize(column.width, height),
                    colorScheme = colorScheme,
                    isExtension = currentIsExtension
            )
            currentWidth += column.width
            currentIsExtension = true
            box
        }
                .toList()
    }

    override fun draw() {
        columnBoxes.forEachIndexed { index, box ->
            box.title(columns[index].title)
            box.draw()
        }
    }

    fun refresh() {
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        for (i in topLine..topLine + height - 2) {
            val relativeIndex = i - topLine
            val tableCells = dataProvider(i)
            for (column in tableCells.indices) {
                val highlight = focusLine == i
                columnBoxes[column].text(tableCells[column].text, relativeIndex, highlight)
            }
            for (column in tableCells.size until columnBoxes.size) {
                val highlight = focusLine == i
                columnBoxes[column].text("", relativeIndex, highlight)
            }
        }
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
