package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.console.graphics.Table

fun main() {
    val defaultTerminalFactory = DefaultTerminalFactory()
    defaultTerminalFactory.setInitialTerminalSize(TerminalSize(120, 30))
    val terminal: Terminal = defaultTerminalFactory.createTerminal()
    val screen = TerminalScreen(terminal)
    screen.startScreen()
    screen.cursorPosition = null

    val background = Background(screen)
    background.draw()

    val tableInstructions = Table(screen, TerminalPosition(1, 2), 10, listOf(5, 15))
    tableInstructions.draw()
    tableInstructions.title(value = "index", column = 0)
    tableInstructions.title(value = "instruction", column = 1)
    tableInstructions.textTable(listOf(
            listOf("0", "PUSH 80"),
            listOf("1", "PUSH 80")
    ))

    val tableStack = Table(screen, TerminalPosition(tableInstructions.right() + 2, 2), 10, listOf(15, 15))
    tableStack.draw()
    tableStack.title(value = "stack address", column = 0)
    tableStack.title(value = "value", column = 1)
    tableStack.textTable(listOf(
            listOf("0", "0x0000"),
            listOf("1", "0x0001")
    ))

    val tableMemory = Table(screen, TerminalPosition(tableStack.right() + 2, 2), 10, listOf(15, 15))
    tableMemory.draw()
    tableMemory.title(value = "memory address", column = 0)
    tableMemory.title(value = "value", column = 1)
    tableMemory.textTable(listOf(
            listOf("0", "0x0000"),
            listOf("1", "0x0001")
    ))

    val tableCallData = Table(screen, TerminalPosition(tableInstructions.right() + 2, tableInstructions.bottom() + 3), 10, listOf(15, 15))
    tableCallData.draw()
    tableCallData.title(value = "call data index", column = 0)
    tableCallData.title(value = "value", column = 1)
    tableCallData.textTable(listOf(
            listOf("0", "0x0000"),
            listOf("1", "0x0001")
    ))

    val tableDisk = Table(screen, TerminalPosition(tableCallData.right() + 2, tableInstructions.bottom() + 3), 10, listOf(15, 15))
    tableDisk.draw()
    tableDisk.title(value = "disk address", column = 0)
    tableDisk.title(value = "value", column = 1)
    tableDisk.textTable(listOf(
            listOf("0", "0x0000"),
            listOf("1", "0x0001")
    ))

    while (true) {
        val keyStroke: KeyStroke? = screen.pollInput()
        if (keyStroke != null && (keyStroke.keyType === KeyType.Escape || keyStroke.keyType === KeyType.EOF)) {
            break
        }
        screen.refresh()
        Thread.yield()
    }
    screen.close()
}
