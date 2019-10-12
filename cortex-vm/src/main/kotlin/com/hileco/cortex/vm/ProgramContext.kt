package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmByteArray
import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.backed.BackedVmByteArray
import com.hileco.cortex.collections.layer.LayeredVmStack
import java.math.BigInteger

class ProgramContext : VmComponent<ProgramContext> {
    val program: Program
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: VmStack<ByteArray>
    val memory: VmByteArray
    var returnDataOffset: BigInteger
    var returnDataSize: BigInteger
    val callData: VmByteArray

    constructor(program: Program) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = LayeredVmStack()
        memory = BackedVmByteArray()
        returnDataOffset = BigInteger.ZERO
        returnDataSize = BigInteger.ZERO
        callData = BackedVmByteArray()
    }

    private constructor(program: Program,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: VmStack<ByteArray>,
                        memory: VmByteArray,
                        returnDataOffset: BigInteger,
                        returnDataSize: BigInteger,
                        callData: VmByteArray) {
        this.program = program
        this.instructionsExecuted = instructionsExecuted
        this.instructionPosition = instructionPosition
        this.stack = stack
        this.memory = memory
        this.returnDataOffset = returnDataOffset
        this.returnDataSize = returnDataSize
        this.callData = callData
    }

    override fun close() {
        program.close()
        stack.close()
        memory.close()
        callData.close()
    }

    override fun copy(): ProgramContext {
        return ProgramContext(program.copy(), instructionsExecuted, instructionPosition, stack.copy(), memory.copy(), returnDataOffset, returnDataSize, callData.copy())
    }
}