package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.BranchedComposite
import com.hileco.cortex.collections.BranchedMap
import com.hileco.cortex.collections.BranchedStack
import com.hileco.cortex.symbolic.expressions.Expression

class SymbolicProgramContext : BranchedComposite<SymbolicProgramContext> {
    val program: SymbolicProgram
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: BranchedStack<Expression>
    val memory: BranchedMap<BackedInteger, Expression>
    var returnDataOffset: BackedInteger
    var returnDataSize: BackedInteger
    val callData: BranchedMap<BackedInteger, Expression>

    constructor(program: SymbolicProgram) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = BranchedStack()
        memory = BranchedMap()
        returnDataOffset = ZERO_32
        returnDataSize = ZERO_32
        callData = BranchedMap()
    }

    private constructor(program: SymbolicProgram,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: BranchedStack<Expression>,
                        memory: BranchedMap<BackedInteger, Expression>,
                        returnDataOffset: BackedInteger,
                        returnDataSize: BackedInteger,
                        callData: BranchedMap<BackedInteger, Expression>) {
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