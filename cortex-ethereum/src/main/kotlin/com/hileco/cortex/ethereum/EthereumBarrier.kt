package com.hileco.cortex.ethereum

import com.hileco.cortex.vm.instructions.Instruction

data class EthereumBarrier(var id: String,
                           var contractAddress: String,
                           var contractCode: String,
                           var ethereumInstructions: List<EthereumInstruction>,
                           var cortexInstructions: List<Instruction>)