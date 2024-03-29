package com.hileco.cortex.symbolic.instructions.stack

enum class ExecutionVariable {
    ADDRESS_SELF,
    ADDRESS_CALLER,
    ADDRESS_ORIGIN,
    START_TIME,
    INSTRUCTION_POSITION,
    TRANSACTION_CALL_DATA_SIZE,
    TRANSACTION_FUNDS,
    PROGRAM_MEMORY_SIZE,
    PROGRAM_CODE_SIZE,
    TRANSACTION_GAS_REMAINING,
    TRANSACTION_GAS_PRICE,
    BLOCK_COINBASE,
    BLOCK_TIMESTAMP,
    BLOCK_NUMBER,
    BLOCK_DIFFICULTY,
    BLOCK_GAS_LIMIT
}
