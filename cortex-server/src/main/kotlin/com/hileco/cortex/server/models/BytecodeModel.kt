package com.hileco.cortex.server.models

import com.hileco.cortex.ethereum.EthereumInstruction
import com.hileco.cortex.vm.instructions.Instruction

data class BytecodeModel(
        val bytecode: String,
        val cortexInstructions: List<Instruction>,
        val ethereumInstructions: List<EthereumInstruction>
)
