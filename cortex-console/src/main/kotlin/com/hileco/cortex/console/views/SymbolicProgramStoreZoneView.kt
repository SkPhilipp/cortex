package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger

class SymbolicProgramStoreZoneView(private val programStoreZone: ProgramStoreZone,
                                   screen: TerminalScreen,
                                   position: TerminalPosition,
                                   height: Int) {
    private var values: VmMap<BigInteger, Expression> = LayeredVmMap()
    private val table: Table = Table(screen, position, height, listOf(15, 25))

    fun bottom(): Int {
        return table.bottom()
    }

    fun right(): Int {
        return table.right()
    }

    fun draw() {
        table.draw()
        table.title(value = "$programStoreZone address", column = 0)
        table.title(value = "value", column = 1)
    }

    fun drawFocus(focusLine: Int) {
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size()
        for (i in topLine..topLine + table.height - 2) {
            val relativeIndex = i - topLine
            val values = if (i < valuesSize) {
                val expression = values[BigInteger.valueOf(i.toLong())]
                listOf("$i", "$expression")
            } else {
                listOf("", "")
            }
            table.textRow(values, relativeIndex, focusLine == i)
        }
    }

    fun drawContent(values: VmMap<BigInteger, Expression>) {
        this.values = values
        drawFocus(0)
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}