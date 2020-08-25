package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmByteArray
import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.backed.BackedVmByteArray
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.instructions.Instruction

class Program : VmComponent<Program> {
    val instructions: List<Instruction>
    val instructionsRelative: List<PositionedInstruction>
    val instructionsAbsolute: Map<Int, PositionedInstruction>
    val address: BackedInteger
    val storage: VmByteArray
    val transfers: VmStack<Pair<BackedInteger, BackedInteger>>

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
        this.address = address
        this.storage = BackedVmByteArray()
        this.transfers = LayeredVmStack()
    }

    private constructor(instructions: List<Instruction>,
                        instructionsRelative: List<PositionedInstruction>,
                        instructionsAbsolute: Map<Int, PositionedInstruction>,
                        address: BackedInteger,
                        storage: VmByteArray,
                        transfers: VmStack<Pair<BackedInteger, BackedInteger>>) {
        this.instructions = instructions
        this.instructionsAbsolute = instructionsAbsolute
        this.instructionsRelative = instructionsRelative
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun close() {
        storage.close()
        transfers.close()
    }

    override fun copy(): Program {
        return Program(instructions, instructionsRelative, instructionsAbsolute, address, storage.copy(), transfers.copy())
    }
}
