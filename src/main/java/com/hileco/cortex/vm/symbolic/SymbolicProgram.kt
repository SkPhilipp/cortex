package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class SymbolicProgram : Layered<SymbolicProgram> {
    val instructions: List<Instruction>
    val address: BigInteger
    val storage: LayeredMap<BigInteger, Expression>
    val transfers: LayeredStack<Pair<Expression, Expression>>

    constructor(instructions: List<Instruction>,
                address: BigInteger = BigInteger.ZERO) {
        this.instructions = instructions
        this.address = address
        storage = LayeredMap()
        transfers = LayeredStack()
    }

    private constructor(instructions: List<Instruction>,
                        address: BigInteger,
                        storage: LayeredMap<BigInteger, Expression>,
                        transfers: LayeredStack<Pair<Expression, Expression>>) {
        this.instructions = instructions
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun branch(): SymbolicProgram {
        return SymbolicProgram(instructions, address, storage.branch(), transfers.branch())
    }

    override fun close() {
        storage.close()
        transfers.close()
    }
}
