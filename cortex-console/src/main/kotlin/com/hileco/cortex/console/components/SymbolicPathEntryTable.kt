package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry

class SymbolicPathEntryTable(screen: TerminalScreen,
                             override val position: TerminalPosition,
                             height: Int) : Component {
    private var values: VmStack<SymbolicPathEntry> = LayeredVmStack()
    val delegate: Table

    init {
        delegate = Table(screen,
                position,
                height,
                listOf(
                        TableColumn("index", 9),
                        TableColumn("source", 9),
                        TableColumn("target", 9),
                        TableColumn("condition", 55)
                ),
                { index ->
                    if (index < values.size()) {
                        val entry = values[index]
                        val cellIndex = TableCell("$index")
                        val cellSource = TableCell("${entry.source}")
                        val cellTarget = TableCell("${entry.target}")
                        val cellCondition = TableCell("${if (entry.taken) entry.condition else Expression.Not(entry.condition)}")
                        listOf(cellIndex, cellSource, cellTarget, cellCondition)
                    } else {
                        listOf()
                    }
                }
        )
    }

    fun content(values: VmStack<SymbolicPathEntry> = this.values,
                focusLine: Int = delegate.focusLine) {
        this.values = values
        delegate.focusLine = focusLine
    }

    override val bottom: Int
        get() {
            return delegate.bottom
        }

    override val right: Int
        get() {
            return delegate.right
        }

    override fun draw() {
        delegate.draw()
        delegate.refresh()
    }
}
