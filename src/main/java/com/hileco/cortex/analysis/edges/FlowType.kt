package com.hileco.cortex.analysis.edges

enum class FlowType(val isJumping: Boolean,
                    val isConditional: Boolean,
                    val isDynamic: Boolean) {
    PROGRAM_FLOW(false, false, false),
    PROGRAM_END(false, false, false),
    INSTRUCTION_JUMP_IF(true, false, false),
    INSTRUCTION_JUMP(true, true, false),
    INSTRUCTION_JUMP_IF_DYNAMIC(true, false, true),
    INSTRUCTION_JUMP_DYNAMIC(true, true, true)
}
