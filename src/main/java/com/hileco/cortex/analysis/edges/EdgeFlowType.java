package com.hileco.cortex.analysis.edges;

public enum EdgeFlowType {
    BLOCK_PART,
    BLOCK_END,
    START,
    END,
    INSTRUCTION_CALL,
    INSTRUCTION_CALL_RETURN,
    INSTRUCTION_JUMP_IF,
    INSTRUCTION_JUMP,
    INSTRUCTION_EXIT,
    INSTRUCTION_HALT
}
