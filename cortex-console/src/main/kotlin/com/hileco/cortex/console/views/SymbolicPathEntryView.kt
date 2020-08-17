package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry

class SymbolicPathEntryView(screen: TerminalScreen,
                            position: TerminalPosition,
                            height: Int) {
    private var values: VmStack<SymbolicPathEntry> = LayeredVmStack()
    private val table: Table = Table(screen, position, height, listOf(9, 9, 9, 55))

    fun bottom(): Int {
        return table.bottom()
    }

    fun right(): Int {
        return table.right()
    }

    fun draw() {
        table.draw()
        table.title(value = "index", column = 0)
        table.title(value = "source", column = 1)
        table.title(value = "target", column = 2)
        table.title(value = "condition", column = 3)
    }

    fun drawContent(values: VmStack<SymbolicPathEntry>, focusLine: Int = -1) {
        this.values = values
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        for (i in topLine..topLine + table.height - 2) {
            val relativeIndex = i - topLine
            val row = if (i < valuesSize) {
                val symbolicPathEntry = values.peek(i)
                listOf("$i",
                        "${symbolicPathEntry.source}",
                        "${symbolicPathEntry.target}",
                        "${if (symbolicPathEntry.taken) symbolicPathEntry.condition else Expression.Not(symbolicPathEntry.condition)}")
            } else {
                listOf("", "", "", "")
            }
            table.textRow(row, relativeIndex, focusLine == i)
        }
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}