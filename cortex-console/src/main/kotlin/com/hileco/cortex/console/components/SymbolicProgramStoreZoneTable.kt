package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.collections.BranchedMap
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.symbolic.ProgramStoreZone
import com.hileco.cortex.symbolic.expressions.Expression

class SymbolicProgramStoreZoneTable(programStoreZone: ProgramStoreZone,
                                    screen: TerminalScreen,
                                    override val position: TerminalPosition,
                                    height: Int) : Component {
    private var values: BranchedMap<BackedInteger, Expression> = BranchedMap()
    private val delegate: Table

    init {
        delegate = Table(screen,
                position,
                height,
                listOf(
                        TableColumn("$programStoreZone address", 16),
                        TableColumn("value", 25)
                ),
                { index ->
                    val keys = values.keys.sorted()
                    if (index < keys.size) {
                        val key = keys[index]
                        val expression = values[key]
                        listOf(
                                TableCell("$key"),
                                TableCell("$expression")
                        )
                    } else {
                        listOf()
                    }
                }
        )
    }

    fun content(values: BranchedMap<BackedInteger, Expression> = this.values,
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
