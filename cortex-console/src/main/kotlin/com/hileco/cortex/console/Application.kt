package com.hileco.cortex.console

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.console.views.InstructionsView
import com.hileco.cortex.console.views.SymbolicProgramStoreZoneView
import com.hileco.cortex.console.views.SymbolicStackView
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.instructions.stack.PUSH
import java.math.BigInteger

fun randomVmMap(): VmMap<BigInteger, Expression> {
    val layeredVmMap = LayeredVmMap<BigInteger, Expression>()
    layeredVmMap[BigInteger.valueOf(0)] = Expression.Value(0)
    layeredVmMap[BigInteger.valueOf(1)] = Expression.Add(Expression.Value(1), Expression.Value(2))
    layeredVmMap[BigInteger.valueOf(2)] = Expression.Value(3)
    layeredVmMap[BigInteger.valueOf(3)] = Expression.Value(4)
    return layeredVmMap
}

fun randomVmStack(): VmStack<Expression> {
    val layeredVmStack = LayeredVmStack<Expression>()
    layeredVmStack.push(Expression.Value(0))
    layeredVmStack.push(Expression.Add(Expression.Value(1), Expression.Value(2)))
    layeredVmStack.push(Expression.Value(3))
    layeredVmStack.push(Expression.Value(4))
    return layeredVmStack
}

fun main() {
    val defaultTerminalFactory = DefaultTerminalFactory()
    defaultTerminalFactory.setInitialTerminalSize(TerminalSize(120, 27))
    val terminal: Terminal = defaultTerminalFactory.createTerminal()
    val screen = TerminalScreen(terminal)
    screen.startScreen()
    screen.cursorPosition = null

    val background = Background(screen)
    background.draw()

    val instructionsView = InstructionsView(screen, TerminalPosition(1, 2), 23)
    instructionsView.draw()
    instructionsView.drawContent(listOf(
            PUSH(80),
            PUSH(81),
            PUSH(82),
            PUSH(83),
            PUSH(84),
            PUSH(85),
            PUSH(86),
            PUSH(87),
            PUSH(88),
            PUSH(89),
            PUSH(90),
            PUSH(91)
    ))

    val stackView = SymbolicStackView(screen, TerminalPosition(instructionsView.right() + 2, 2), 10)
    stackView.draw()
    stackView.drawContent(randomVmStack())

    val memoryView = SymbolicProgramStoreZoneView(ProgramStoreZone.MEMORY, screen, TerminalPosition(stackView.right() + 2, 2), 10);
    memoryView.draw()
    memoryView.drawContent(randomVmMap())

    val callDataView = SymbolicProgramStoreZoneView(ProgramStoreZone.CALL_DATA, screen, TerminalPosition(instructionsView.right() + 2, stackView.bottom() + 3), 10);
    callDataView.draw()
    callDataView.drawContent(randomVmMap())

    val diskView = SymbolicProgramStoreZoneView(ProgramStoreZone.DISK, screen, TerminalPosition(callDataView.right() + 2, memoryView.bottom() + 3), 10);
    diskView.draw()
    diskView.drawContent(randomVmMap())

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
