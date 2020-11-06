package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.LayeredVmMap
import com.hileco.cortex.collections.LayeredVmStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32

class SymbolicProgramContext : VmComponent<SymbolicProgramContext> {
    val program: SymbolicProgram
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: VmStack<Expression>
    val memory: LayeredVmMap<BackedInteger, Expression>
    var returnDataOffset: BackedInteger
    var returnDataSize: BackedInteger
    val callData: LayeredVmMap<BackedInteger, Expression>

    constructor(program: SymbolicProgram) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = LayeredVmStack()
        memory = LayeredVmMap()
        returnDataOffset = ZERO_32
        returnDataSize = ZERO_32
        callData = LayeredVmMap()
    }

    private constructor(program: SymbolicProgram,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: VmStack<Expression>,
                        memory: LayeredVmMap<BackedInteger, Expression>,
                        returnDataOffset: BackedInteger,
                        returnDataSize: BackedInteger,
                        callData: LayeredVmMap<BackedInteger, Expression>) {
        this.program = program
        this.instructionsExecuted = instructionsExecuted
        this.instructionPosition = instructionPosition
        this.stack = stack
        this.memory = memory
        this.returnDataOffset = returnDataOffset
        this.returnDataSize = returnDataSize
        this.callData = callData
    }

    override fun copy(): SymbolicProgramContext {
        return SymbolicProgramContext(program.copy(), instructionsExecuted, instructionPosition, stack.copy(), memory.copy(), returnDataOffset, returnDataSize, callData.copy())
    }

    override fun close() {
        program.close()
        stack.close()
        memory.close()
        callData.close()
    }
}