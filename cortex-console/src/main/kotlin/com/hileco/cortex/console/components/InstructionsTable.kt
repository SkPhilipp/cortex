package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.vm.instructions.Instruction

class InstructionsTable(screen: TerminalScreen,
                        override val position: TerminalPosition,
                        height: Int) : Component {
    private var values: List<Instruction> = listOf()
    val delegate: Table

    init {
        delegate = Table(screen,
                position,
                height,
                listOf(
                        TableColumn("index", 7),
                        TableColumn("instruction", 30)
                ),
                { index ->
                    if (index < values.size) {
                        val entry = values[index]
                        val cellIndex = TableCell("$index")
                        val cellRow = TableCell("$entry")
                        listOf(cellIndex, cellRow)
                    } else {
                        listOf()
                    }
                }
        )
    }

    fun content(values: List<Instruction> = this.values,
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
