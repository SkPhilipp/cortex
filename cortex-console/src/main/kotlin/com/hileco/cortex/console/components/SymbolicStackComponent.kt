package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression

class SymbolicStackComponent(screen: TerminalScreen,
                             initialPosition: TerminalPosition,
                             height: Int) : DelegatingComponent<Table>(Table(screen, initialPosition, height, listOf(15, 25))) {
    private var values: VmStack<Expression> = LayeredVmStack()
    private var focusLine: Int = -1

    override fun draw() {
        delegate.draw()
        delegate.title(value = "index", column = 0)
        delegate.title(value = "value", column = 1)
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        for (i in topLine..topLine + delegate.height - 2) {
            val relativeIndex = i - topLine
            val row = if (i < valuesSize) {
                val expression = values.peek(i)
                listOf("$i", "$expression")
            } else {
                listOf("", "")
            }
            delegate.textRow(row, relativeIndex, focusLine == i)
        }
    }

    fun content(values: VmStack<Expression>, focusLine: Int = -1) {
        this.values = values
        this.focusLine = focusLine
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
