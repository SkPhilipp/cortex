package com.hileco.cortex.console.compositions

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.screen.TerminalScreen
import com.hileco.cortex.console.components.*
import com.hileco.cortex.console.graphics.Background
import com.hileco.cortex.console.graphics.Box
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramStoreZone

class SymbolicProgramComposition(screen: TerminalScreen,
                                 var virtualMachine: SymbolicVirtualMachine,
                                 private val initialPosition: TerminalPosition) : Component {

    override val position: TerminalPosition
        get() = initialPosition
    override val bottom: Int
        get() = pathView.bottom
    override val right: Int
        get() = pathView.right

    private val instructionsView = InstructionsComponent(screen, initialPosition, 23)
    private val stackView = SymbolicStackComponent(screen, TerminalPosition(instructionsView.right + 2, initialPosition.row), 10)
    private val memoryView = SymbolicProgramStoreZoneComponent(ProgramStoreZone.MEMORY, screen, TerminalPosition(stackView.right + 2, initialPosition.row), 10)
    private val callDataView = SymbolicProgramStoreZoneComponent(ProgramStoreZone.CALL_DATA, screen, TerminalPosition(instructionsView.right + 2, stackView.bottom + 3), 10)
    private val diskView = SymbolicProgramStoreZoneComponent(ProgramStoreZone.DISK, screen, TerminalPosition(callDataView.right + 2, memoryView.bottom + 3), 10)
    private val exitView = Box(screen, TerminalPosition(initialPosition.column, instructionsView.bottom + 3), TerminalSize(27, 3))
    private val pathView = SymbolicPathEntryComponent(screen, TerminalPosition(exitView.right() + 2, instructionsView.bottom + 3), 10)
    private val background = Background(screen)

    override fun draw() {
        val instructions = virtualMachine.programs.first().program.instructions
        val positionedInstructions = virtualMachine.programs.first().program.instructionsAbsolute
        val instructionPosition = virtualMachine.programs.first().instructionPosition
        val positionedInstruction = positionedInstructions[instructionPosition] ?: throw IllegalStateException()
        background.draw()
        instructionsView.content(instructions = instructions, focusLine = positionedInstruction.relativePosition)
        instructionsView.draw()
        stackView.content(values = virtualMachine.programs.first().stack)
        stackView.draw()
        memoryView.content(values = virtualMachine.programs.first().memory)
        memoryView.draw()
        callDataView.content(values = virtualMachine.programs.first().callData)
        callDataView.draw()
        diskView.content(values = virtualMachine.programs.first().program.storage)
        diskView.draw()
        exitView.title(value = "state")
        exitView.draw()
        pathView.content(values = virtualMachine.path)
        pathView.draw()
        val exitedReason = virtualMachine.exitedReason
        if (exitedReason != null) {
            exitView.text(exitedReason.name)
        }
    }
}
