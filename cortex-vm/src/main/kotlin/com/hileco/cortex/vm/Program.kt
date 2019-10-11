package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmByteArray
import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.backed.BackedVmByteArray
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.vm.instructions.Instruction
import java.math.BigInteger

class Program : VmComponent<Program> {
    val instructions: List<Instruction>
    val address: BigInteger
    val storage: VmByteArray
    val transfers: VmStack<Pair<BigInteger, BigInteger>>

    constructor(instructions: List<Instruction>,
                address: BigInteger = BigInteger.ZERO) {
        this.instructions = instructions
        this.address = address
        this.storage = BackedVmByteArray()
        this.transfers = LayeredVmStack()
    }

    private constructor(instructions: List<Instruction>,
                        address: BigInteger,
                        storage: VmByteArray,
                        transfers: VmStack<Pair<BigInteger, BigInteger>>) {
        this.instructions = instructions
        this.address = address
        this.storage = storage
        this.transfers = transfers
    }

    override fun close() {
        storage.close()
        transfers.close()
    }

    override fun copy(): Program {
        return Program(instructions, address, storage.copy(), transfers.copy())
    }
}
