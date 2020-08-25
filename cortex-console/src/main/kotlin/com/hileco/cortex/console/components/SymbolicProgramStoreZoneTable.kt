package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.console.graphics.TableCell
import com.hileco.cortex.console.graphics.TableColumn
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.bytes.BackedInteger

class SymbolicProgramStoreZoneTable(programStoreZone: ProgramStoreZone,
                                    screen: TerminalScreen,
                                    override val position: TerminalPosition,
                                    height: Int) : Component {
    private var values: VmMap<BackedInteger, Expression> = LayeredVmMap()
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
                    val keys = values.keySet().sorted()
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

    fun content(values: VmMap<BackedInteger, Expression> = this.values,
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
