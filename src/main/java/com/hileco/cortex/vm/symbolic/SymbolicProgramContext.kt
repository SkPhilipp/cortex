package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class SymbolicProgramContext : DelegateLayered<SymbolicProgramContext> {
    val program: SymbolicProgram
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: LayeredStack<Expression>
    val memory: LayeredMap<BigInteger, Expression>
    var returnDataOffset: BigInteger
    var returnDataSize: BigInteger
    val callData: LayeredMap<BigInteger, Expression>

    constructor(program: SymbolicProgram) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = LayeredStack()
        memory = LayeredMap()
        returnDataOffset = BigInteger.ZERO
        returnDataSize = BigInteger.ZERO
        callData = LayeredMap()
    }

    private constructor(program: SymbolicProgram,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: LayeredStack<Expression>,
                        memory: LayeredMap<BigInteger, Expression>,
                        returnDataOffset: BigInteger,
                        returnDataSize: BigInteger,
                        callData: LayeredMap<BigInteger, Expression>) {
        this.program = program
        this.instructionsExecuted = instructionsExecuted
        this.instructionPosition = instructionPosition
        this.stack = stack
        this.memory = memory
        this.returnDataOffset = returnDataOffset
        this.returnDataSize = returnDataSize
        this.callData = callData
    }

    override fun recreateParent(): SymbolicProgramContext {
        return SymbolicProgramContext(program.parent(), instructionsExecuted, instructionPosition, stack.parent(), memory.parent(), returnDataOffset, returnDataSize, callData.parent())
    }

    override fun branchDelegates(): SymbolicProgramContext {
        return SymbolicProgramContext(program.branch(), instructionsExecuted, instructionPosition, stack.branch(), memory.branch(), returnDataOffset, returnDataSize, callData.branch())
    }

    override fun disposeDelegates() {
        program.dispose()
        stack.dispose()
        memory.dispose()
        callData.clear()
    }
}