package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression

class SymbolicStackView(screen: TerminalScreen,
                        position: TerminalPosition,
                        height: Int) {
    private var values: VmStack<Expression> = LayeredVmStack()
    private val table: Table = Table(screen, position, height, listOf(15, 25))

    fun bottom(): Int {
        return table.bottom()
    }

    fun right(): Int {
        return table.right()
    }

    fun draw() {
        table.draw()
        table.title(value = "index", column = 0)
        table.title(value = "value", column = 1)
    }

    fun drawContent(values: VmStack<Expression>, focusLine: Int = -1) {
        this.values = values
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        for (i in topLine..topLine + table.height - 2) {
            val relativeIndex = i - topLine
            val row = if (i < valuesSize) {
                val expression = values.peek(i)
                listOf("$i", "$expression")
            } else {
                listOf("", "")
            }
            table.textRow(row, relativeIndex, focusLine == i)
        }
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}