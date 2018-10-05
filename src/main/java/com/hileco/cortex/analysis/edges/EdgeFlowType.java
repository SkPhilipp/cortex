package com.hileco.cortex.analysis.edges;

public enum EdgeFlowType {
    BLOCK_END,
    PROGRAM_START,
    PROGRAM_END,
    INSTRUCTION_CALL,
    INSTRUCTION_CALL_RETURN,
    INSTRUCTION_JUMP_IF,
    INSTRUCTION_JUMP,
    INSTRUCTION_EXIT,
    INSTRUCTION_HALT
}
