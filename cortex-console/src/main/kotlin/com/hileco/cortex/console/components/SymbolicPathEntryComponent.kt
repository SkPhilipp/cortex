package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry

class SymbolicPathEntryComponent(screen: TerminalScreen,
                                 initialPosition: TerminalPosition,
                                 height: Int) : DelegatingComponent<Table>(Table(screen, initialPosition, height, listOf(9, 9, 9, 55))) {
    private var values: VmStack<SymbolicPathEntry> = LayeredVmStack()
    private var focusLine: Int = -1

    override fun draw() {
        delegate.draw()
        delegate.title(value = "index", column = 0)
        delegate.title(value = "source", column = 1)
        delegate.title(value = "target", column = 2)
        delegate.title(value = "condition", column = 3)
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        for (i in topLine..topLine + delegate.height - 2) {
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
            delegate.textRow(row, relativeIndex, focusLine == i)
        }
    }

    fun content(values: VmStack<SymbolicPathEntry> = this.values,
                focusLine: Int = this.focusLine) {
        this.values = values
        this.focusLine = focusLine
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
