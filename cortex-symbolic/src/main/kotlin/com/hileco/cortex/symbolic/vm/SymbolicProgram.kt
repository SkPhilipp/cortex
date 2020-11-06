package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.BranchedMap
import com.hileco.cortex.collections.Branched
import com.hileco.cortex.symbolic.PositionedInstruction
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.instructions.Instruction

class SymbolicProgram : Branched<SymbolicProgram> {
    val instructions: List<Instruction>
    private val instructionsRelative: List<PositionedInstruction>
    val instructionsAbsolute: Map<Int, PositionedInstruction>
    val instructionsLastPosition: Int
    val address: BackedInteger
    val storage: BranchedMap<BackedInteger, Expression>

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
        this.storage = BranchedMap()
    }

    private constructor(instructions: List<Instruction>,
                        instructionsRelative: List<PositionedInstruction>,
                        instructionsAbsolute: Map<Int, PositionedInstruction>,
                        instructionsLastPosition: Int,
                        address: BackedInteger,
                        storage: BranchedMap<BackedInteger, Expression>) {
        this.instructions = instructions
        this.instructionsRelative = instructionsRelative
        this.instructionsAbsolute = instructionsAbsolute
        this.instructionsLastPosition = instructionsLastPosition
        this.address = address
        this.storage = storage
    }

    override fun close() {
        storage.close()
    }

    override fun copy(): SymbolicProgram {
        return SymbolicProgram(instructions, instructionsRelative, instructionsAbsolute, instructionsLastPosition, address, storage.copy())
    }
}
