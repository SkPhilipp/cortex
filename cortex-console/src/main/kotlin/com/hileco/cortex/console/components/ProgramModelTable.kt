package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.processing.database.ProgramModel

class ProgramModelTable(screen: TerminalScreen,
                        override val position: TerminalPosition,
                        height: Int) : Component {
    private var values: List<ProgramModel> = listOf()
    val delegate: Table

    init {
        delegate = Table(screen,
                position,
                height,
                listOf(
                        TableColumn("network", 12),
                        TableColumn("address", 48),
                        TableColumn("histogram", 42),
                        TableColumn("identified as", 15)
                ),
                { index ->
                    if (index < values.size) {
                        val entry = values[index]
                        listOf(
                                TableCell(entry.location.networkName),
                                TableCell(entry.location.programAddress),
                                TableCell(entry.histogram),
                                TableCell(entry.identifiedAs)
                        )
                    } else {
                        listOf()
                    }
                }
        )
    }

    fun content(values: List<ProgramModel> = this.values,
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
