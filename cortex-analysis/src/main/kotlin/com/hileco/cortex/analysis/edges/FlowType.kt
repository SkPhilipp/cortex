package com.hileco.cortex.analysis.edges

enum class FlowType(val jumps: Boolean,
                    val conditional: Boolean,
                    val dynamic: Boolean) {
    PROGRAM_FLOW(false, false, false),
    PROGRAM_END(false, false, false),
    INSTRUCTION_JUMP_IF(true, false, false),
    INSTRUCTION_JUMP(true, true, false),
    INSTRUCTION_JUMP_IF_DYNAMIC(true, false, true),
    INSTRUCTION_JUMP_DYNAMIC(true, true, true)
}
