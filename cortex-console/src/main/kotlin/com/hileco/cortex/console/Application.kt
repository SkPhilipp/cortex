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

fun randomData(): List<List<String>> {
    return listOf(
            listOf("0x000000000000", "0x000000000000"),
            listOf("0x000000000001", "0x000000000001"),
            listOf("00x00000000002", "0x000000000002"),
            listOf("00x00000000003", "0x000000000003"),
            listOf("00x00000000004", "0x000000000004"),
            listOf("00x00000000005", "0x000000000005"),
            listOf("0x000000000006", "0x000000000006"),
            listOf("0x000000000007", "0x000000000007"),
            listOf("0x000000000008", "0x000000000008"),
            listOf("0x000000000009", "0x000000000009"),
            listOf("0x000000000010", "0x000000000010")
    )
}

fun main() {
    val defaultTerminalFactory = DefaultTerminalFactory()
    defaultTerminalFactory.setInitialTerminalSize(TerminalSize(110, 27))
    val terminal: Terminal = defaultTerminalFactory.createTerminal()
    val screen = TerminalScreen(terminal)
    screen.startScreen()
    screen.cursorPosition = null

    val background = Background(screen)
    background.draw()

    val tableInstructions = Table(screen, TerminalPosition(1, 2), 10, listOf(6, 15))
    tableInstructions.draw()
    tableInstructions.title(value = "index", column = 0)
    tableInstructions.title(value = "instruction", column = 1)
    tableInstructions.textTable(listOf(
            listOf("00000", "PUSH 80"),
            listOf("00001", "PUSH 80"),
            listOf("00002", "PUSH 80"),
            listOf("00003", "PUSH 80"),
            listOf("00004", "PUSH 80"),
            listOf("00005", "PUSH 80"),
            listOf("00006", "PUSH 80"),
            listOf("00007", "PUSH 80"),
            listOf("00008", "PUSH 80"),
            listOf("00009", "PUSH 80"),
            listOf("00010", "PUSH 80")
    ))

    val tableStack = Table(screen, TerminalPosition(tableInstructions.right() + 2, 2), 10, listOf(15, 25))
    tableStack.draw()
    tableStack.title(value = "stack address", column = 0)
    tableStack.title(value = "value", column = 1)
    tableStack.textTable(randomData())

    val tableMemory = Table(screen, TerminalPosition(tableStack.right() + 2, 2), 10, listOf(15, 25))
    tableMemory.draw()
    tableMemory.title(value = "memory address", column = 0)
    tableMemory.title(value = "value", column = 1)
    tableMemory.textTable(randomData())

    val tableCallData = Table(screen, TerminalPosition(tableInstructions.right() + 2, tableInstructions.bottom() + 3), 10, listOf(15, 25))
    tableCallData.draw()
    tableCallData.title(value = "call address", column = 0)
    tableCallData.title(value = "value", column = 1)
    tableCallData.textTable(randomData())

    val tableDisk = Table(screen, TerminalPosition(tableCallData.right() + 2, tableInstructions.bottom() + 3), 10, listOf(15, 25))
    tableDisk.draw()
    tableDisk.title(value = "disk address", column = 0)
    tableDisk.title(value = "value", column = 1)
    tableDisk.textTable(randomData())

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
