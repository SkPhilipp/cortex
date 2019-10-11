package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.Expression
import java.math.BigInteger

class SymbolicProgramContext : VmComponent<SymbolicProgramContext> {
    val program: SymbolicProgram
    var instructionsExecuted: Int
    var instructionPosition: Int
    val stack: VmStack<Expression>
    val memory: VmMap<BigInteger, Expression>
    var returnDataOffset: BigInteger
    var returnDataSize: BigInteger
    val callData: VmMap<BigInteger, Expression>

    constructor(program: SymbolicProgram) {
        this.program = program
        instructionsExecuted = 0
        instructionPosition = 0
        stack = LayeredVmStack()
        memory = LayeredVmMap()
        returnDataOffset = BigInteger.ZERO
        returnDataSize = BigInteger.ZERO
        callData = LayeredVmMap()
    }

    private constructor(program: SymbolicProgram,
                        instructionsExecuted: Int,
                        instructionPosition: Int,
                        stack: VmStack<Expression>,
                        memory: VmMap<BigInteger, Expression>,
                        returnDataOffset: BigInteger,
                        returnDataSize: BigInteger,
                        callData: VmMap<BigInteger, Expression>) {
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