package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class SymbolicProgram : DelegateLayered<SymbolicProgram> {
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

    override fun recreateParent(): SymbolicProgram {
        return SymbolicProgram(instructions, address, storage.parent(), transfers.parent())
    }

    override fun branchDelegates(): SymbolicProgram {
        return SymbolicProgram(instructions, address, storage.branch(), transfers.branch())
    }

    override fun closeDelegates() {
        storage.close()
        transfers.close()
    }
}
