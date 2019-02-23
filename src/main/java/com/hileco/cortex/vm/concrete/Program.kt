package com.hileco.cortex.vm.concrete

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class Program : Layered<Program> {
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

    override fun branch(): Program {
        return Program(instructions, address, storage.branch(), transfers.branch())
    }

    override fun close() {
        storage.close()
        transfers.close()
    }
}
