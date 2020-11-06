package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.BranchedStack
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.symbolic.expressions.Expression

class SymbolicStackTable(screen: TerminalScreen,
                         override val position: TerminalPosition,
                         height: Int) : Component {
    private var values: BranchedStack<Expression> = BranchedStack()
    private val delegate: Table

    init {
        delegate = Table(screen,
                position,
                height,
                listOf(
                        TableColumn("index", 15),
                        TableColumn("value", 25)
                ),
                { index ->
                    if (index < values.size()) {
                        val entry = values[index]
                        listOf(
                                TableCell("$index"),
                                TableCell("$entry")
                        )
                    } else {
                        listOf()
                    }
                }
        )
    }

    fun content(values: BranchedStack<Expression> = this.values,
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
