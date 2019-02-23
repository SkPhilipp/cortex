package com.hileco.cortex.instructions

import com.hileco.cortex.vm.concrete.ProgramContext

class ProgramException(val programContext: ProgramContext,
                       val reason: Reason) : Exception() {
    override val message: String?
        get() = "$reason: $programContext"

    enum class Reason {
        JUMP_TO_ILLEGAL_INSTRUCTION,
        STORAGE_ACCESS_OUT_OF_BOUNDS,
        JUMP_OUT_OF_BOUNDS,
        INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL,
        INSTRUCTION_LIMIT_REACHED_ON_PROGRAM_LEVEL,
        STACK_LIMIT_REACHED,
        RETURN_DATA_TOO_LARGE,
        STACK_TOO_FEW_ELEMENTS,
        CALL_RECIPIENT_MISSING,
        WINNER
    }
}
