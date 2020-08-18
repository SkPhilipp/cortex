package com.hileco.cortex.console.components

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.graphics.Table
import com.hileco.cortex.vm.instructions.Instruction

class InstructionsComponent(screen: TerminalScreen,
                            initialPosition: TerminalPosition,
                            height: Int) : DelegatingComponent<Table>(Table(screen, initialPosition, height, listOf(7, 30))) {
    private var instructions: List<Instruction> = listOf()
    private var focusLine: Int = -1

    override fun draw() {
        delegate.draw()
        delegate.title(value = "index", column = 0)
        delegate.title(value = "instruction", column = 1)
        val topLine = (focusLine - TOP_OFFSET).coerceAtLeast(0)
        val instructionsSize = instructions.size
        for (i in topLine..topLine + delegate.height - 2) {
            val relativeIndex = i - topLine
            val values = if (i < instructionsSize) {
                val instruction = instructions[i]
                listOf("$i", "$instruction")
            } else {
                listOf("", "")
            }
            delegate.textRow(values, relativeIndex, focusLine == i)
        }
    }

    fun content(instructions: List<Instruction> = this.instructions,
                focusLine: Int = this.focusLine) {
        this.instructions = instructions
        this.focusLine = focusLine
    }

    companion object {
        private const val TOP_OFFSET = 2
    }
}
