package com.hileco.cortex.vm.concrete

import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class ProgramContext : Layered<ProgramContext> {
    val program: Program
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: LayeredStack<ByteArray>
    val memory: LayeredBytes
    var returnDataOffset: BigInteger
    var returnDataSize: BigInteger
    val callData: LayeredBytes

    constructor(program: Program) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = LayeredStack()
        memory = LayeredBytes()
        returnDataOffset = BigInteger.ZERO
        returnDataSize = BigInteger.ZERO
        callData = LayeredBytes()
    }

    private constructor(program: Program,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: LayeredStack<ByteArray>,
                        memory: LayeredBytes,
                        returnDataOffset: BigInteger,
                        returnDataSize: BigInteger,
                        callData: LayeredBytes) {
        this.program = program
        this.instructionsExecuted = instructionsExecuted
        this.instructionPosition = instructionPosition
        this.stack = stack
        this.memory = memory
        this.returnDataOffset = returnDataOffset
        this.returnDataSize = returnDataSize
        this.callData = callData
    }

    override fun branch(): ProgramContext {
        return ProgramContext(program.branch(), instructionsExecuted, instructionPosition, stack.branch(), memory.branch(), returnDataOffset, returnDataSize, callData.branch())
    }

    override fun close() {
        program.close()
        stack.close()
        memory.close()
        callData.clear()
    }
}