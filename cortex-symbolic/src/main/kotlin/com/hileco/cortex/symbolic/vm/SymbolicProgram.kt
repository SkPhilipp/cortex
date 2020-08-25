package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.PositionedInstruction
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.instructions.Instruction

class SymbolicProgram : VmComponent<SymbolicProgram> {
    val instructions: List<Instruction>
    val instructionsRelative: List<PositionedInstruction>
    val instructionsAbsolute: Map<Int, PositionedInstruction>
    val instructionsLastPosition: Int
    val address: BackedInteger
    val storage: VmMap<BackedInteger, Expression>
    val transfers: VmStack<Pair<Expression, Expression>>

    constructor(instructions: List<Instruction>,
                address: BackedInteger = ZERO_32) {
        this.instructions = instructions
        var absolutePosition = 0
        var relativePosition = 0
        this.instructionsRelative = this.instructions.map { instruction: Instruction ->
            val position = PositionedInstruction(absolutePosition, relativePosition, instruction)
            absolutePosition += instruction.width
            relativePosition++
            position
        }.toList()
        this.instructionsAbsolute = this.instructionsRelative.map {
            it.absolutePosition to it
        }.toMap()
        this.instructionsLastPosition = absolutePosition
        this.address = address
        this.storage = LayeredVmMap()
        this.transfers = LayeredVmStack()
    }

    private constructor(instructions: List<Instruction>,
                        instructionsRelative: List<PositionedInstruction>,
                        instructionsAbsolute: Map<Int, PositionedInstruction>,
                        instructionsLastPosition: Int,
                        address: BackedInteger,
                        storage: VmMap<BackedInteger, Expression>,
                        transfers: VmStack<Pair<Expression, Expression>>) {
        this.instructions = instructions
        this.instructionsRelative = instructionsRelative
        this.instructionsAbsolute = instructionsAbsolute
        this.instructionsLastPosition = instructionsLastPosition
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun close() {
        storage.close()
        transfers.close()
    }

    override fun copy(): SymbolicProgram {
        return SymbolicProgram(instructions, instructionsRelative, instructionsAbsolute, instructionsLastPosition, address, storage.copy(), transfers.copy())
    }
}
