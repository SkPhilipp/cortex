package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger

class SymbolicProgramStoreZoneComponent(private val programStoreZone: ProgramStoreZone,
                                        screen: TerminalScreen,
                                        initialPosition: TerminalPosition,
                                        height: Int) : DelegatingComponent<Table>(Table(screen, initialPosition, height, listOf(15, 25))) {
    private var values: VmMap<BigInteger, Expression> = LayeredVmMap()
    private var focusLine: Int = -1

    override fun draw() {
        delegate.draw()
        delegate.title(value = "$programStoreZone address", column = 0)
        delegate.title(value = "value", column = 1)
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        val sortedKeys = values.keySet().sorted()
        for (i in topLine..topLine + delegate.height - 2) {
            val relativeIndex = i - topLine
            val row = if (i < valuesSize) {
                val key = sortedKeys[i]
                val expression = values[key]
                listOf("$key", "$expression")
            } else {
                listOf("", "")
            }
            delegate.textRow(row, relativeIndex, focusLine == i)
        }
    }

    fun content(values: VmMap<BigInteger, Expression>, focusLine: Int = -1) {
        this.values = values
        this.focusLine = focusLine
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
