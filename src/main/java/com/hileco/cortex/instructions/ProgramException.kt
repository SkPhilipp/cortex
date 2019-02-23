package com.hileco.cortex.instructions

class ProgramException(val reason: Reason) : Exception() {
    override val message: String?
        get() = "$reason"

    enum class Reason {
        JUMP_TO_ILLEGAL_INSTRUCTION,
        STORAGE_ACCESS_OUT_OF_BOUNDS,
        JUMP_OUT_OF_BOUNDS,
        INSTRUCTION_LIMIT_REACHED_ON_VIRTUAL_MACHINE,
        INSTRUCTION_LIMIT_REACHED_ON_PROGRAM,
        STACK_LIMIT_REACHED,
        RETURN_DATA_TOO_LARGE,
        STACK_TOO_FEW_ELEMENTS,
        CALL_RECIPIENT_MISSING,
        WINNER
    }
}
