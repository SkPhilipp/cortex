package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.processing.database.ProgramModel

class ProgramModelComponent(screen: TerminalScreen,
                            initialPosition: TerminalPosition,
                            height: Int) : DelegatingComponent<Table>(Table(screen, initialPosition, height, listOf(12, 12, 48, 42))) {
    private var values: List<ProgramModel> = listOf()
    var focusLine: Int = -1

    override fun draw() {
        delegate.draw()
        delegate.title(value = "type", column = 0)
        delegate.title(value = "network", column = 1)
        delegate.title(value = "address", column = 2)
        delegate.title(value = "histogram", column = 3)
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val valuesSize = values.size
        for (i in topLine..topLine + delegate.height - 2) {
            val relativeIndex = i - topLine
            val row = if (i < valuesSize) {
                val programModel = values[i]
                listOf(programModel.location.blockchainName,
                        programModel.location.blockchainNetwork,
                        programModel.location.programAddress,
                        programModel.histogram)
            } else {
                listOf("", "", "", "")
            }
            delegate.textRow(row, relativeIndex, focusLine == i)
        }
    }

    fun content(values: List<ProgramModel>, focusLine: Int = -1) {
        this.values = values
        this.focusLine = focusLine
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
