package com.hileco.cortex.vm.concrete

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class Program : DelegateLayered<Program> {
    val instructions: List<Instruction>
    val address: BigInteger
    val storage: LayeredBytes
    val transfers: LayeredStack<Pair<BigInteger, BigInteger>>

    constructor(instructions: List<Instruction>,
                address: BigInteger = BigInteger.ZERO) {
        this.instructions = instructions
        this.address = address
        storage = LayeredBytes()
        transfers = LayeredStack()
    }

    private constructor(instructions: List<Instruction>,
                        address: BigInteger,
                        storage: LayeredBytes,
                        transfers: LayeredStack<Pair<BigInteger, BigInteger>>) {
        this.instructions = instructions
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun recreateParent(): Program {
        return Program(instructions, address, storage.parent(), transfers.parent())
    }

    override fun branchDelegates(): Program {
        return Program(instructions, address, storage.branch(), transfers.branch())
    }

    override fun closeDelegates() {
        storage.close()
        transfers.close()
    }
}
