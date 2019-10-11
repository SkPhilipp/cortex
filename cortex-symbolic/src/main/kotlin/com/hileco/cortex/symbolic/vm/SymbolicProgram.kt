package com.hileco.cortex.symbolic.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.Expression
import com.hileco.cortex.vm.instructions.Instruction
import java.math.BigInteger

class SymbolicProgram : VmComponent<SymbolicProgram> {
    val instructions: List<Instruction>
    val address: BigInteger
    val storage: VmMap<BigInteger, Expression>
    val transfers: VmStack<Pair<Expression, Expression>>

    constructor(instructions: List<Instruction>,
                address: BigInteger = BigInteger.ZERO) {
        this.instructions = instructions
        this.address = address
        this.storage = LayeredVmMap()
        this.transfers = LayeredVmStack()
    }

    private constructor(instructions: List<Instruction>,
                        address: BigInteger,
                        storage: VmMap<BigInteger, Expression>,
                        transfers: VmStack<Pair<Expression, Expression>>) {
        this.instructions = instructions
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun close() {
        storage.close()
        transfers.close()
    }

    override fun copy(): SymbolicProgram {
        return SymbolicProgram(instructions, address, storage.copy(), transfers.copy())
    }
}
