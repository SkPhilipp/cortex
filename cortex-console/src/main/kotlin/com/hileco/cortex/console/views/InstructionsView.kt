package com.hileco.cortex.console.views

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.vm.instructions.Instruction

class InstructionsView(screen: TerminalScreen,
                       position: TerminalPosition,
                       height: Int) {
    private var instructions: List<Instruction> = listOf()
    private val table: Table = Table(screen, position, height, listOf(7, 20))

    fun bottom(): Int {
        return table.bottom()
    }

    fun right(): Int {
        return table.right()
    }

    fun draw() {
        table.draw()
        table.title(value = "index", column = 0)
        table.title(value = "instruction", column = 1)
    }

    fun drawFocus(focusLine: Int) {
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val instructionsSize = instructions.size
        for (i in topLine..topLine + table.height - 2) {
            val relativeIndex = i - topLine
            val values = if (i < instructionsSize) {
                val instruction = instructions[i]
                listOf("$i", "$instruction")
            } else {
                listOf("", "")
            }
            table.textRow(values, relativeIndex, focusLine == i)
        }
    }

    fun drawContent(instructions: List<Instruction>) {
        this.instructions = instructions
        drawFocus(0)
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}