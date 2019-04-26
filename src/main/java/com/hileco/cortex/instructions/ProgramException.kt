package com.hileco.cortex.instructions

class ProgramException(val reason: Reason) : Exception() {
    override val message: String?
        get() = "$reason"

    enum class Reason {
        JUMP_TO_ILLEGAL_INSTRUCTION,
        JUMP_TO_OUT_OF_BOUNDS,
        STORAGE_ACCESS_OUT_OF_BOUNDS,
        REACHED_LIMIT_INSTRUCTIONS_ON_VIRTUAL_MACHINE,
        REACHED_LIMIT_INSTRUCTIONS_ON_PROGRAM,
        STACK_OVERFLOW,
        STACK_UNDERFLOW,
        CALL_RETURN_DATA_TOO_LARGE,
        CALL_RECIPIENT_MISSING,
        WINNER,
        UNKNOWN_INSTRUCTION
    }
}
