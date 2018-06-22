package com.hileco.cortex.instructions;

import java.util.HashMap;
import java.util.Map;

public enum Instruction {

    PUSH,
    POP,
    SWAP,
    DUPLICATE,
    BITWISE_OR,
    BITWISE_XOR,
    BITWISE_AND,
    BITWISE_NOT,
    EQUALS,
    GREATER_THAN,
    LESS_THAN,
    IS_ZERO,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    HASH,
    JUMP,
    JUMP_DESTINATION,
    JUMP_IF,
    LOAD,
    SAVE,
    EXIT;

    public static Map<Class<? extends Instructions.InstructionExecutor>, Instruction> MAPPING;

    static {
        MAPPING = new HashMap<>();
        MAPPING.put(Instructions.Push.class, PUSH);
        MAPPING.put(Instructions.Pop.class, POP);
        MAPPING.put(Instructions.Swap.class, SWAP);
        MAPPING.put(Instructions.Duplicate.class, DUPLICATE);
        MAPPING.put(Instructions.BitwiseOr.class, BITWISE_OR);
        MAPPING.put(Instructions.BitwiseXor.class, BITWISE_XOR);
        MAPPING.put(Instructions.BitwiseAnd.class, BITWISE_AND);
        MAPPING.put(Instructions.BitwiseNot.class, BITWISE_NOT);
        MAPPING.put(Instructions.Equals.class, EQUALS);
        MAPPING.put(Instructions.GreaterThan.class, GREATER_THAN);
        MAPPING.put(Instructions.LessThan.class, LESS_THAN);
        MAPPING.put(Instructions.IsZero.class, IS_ZERO);
        MAPPING.put(Instructions.Add.class, ADD);
        MAPPING.put(Instructions.Subtract.class, SUBTRACT);
        MAPPING.put(Instructions.Multiply.class, MULTIPLY);
        MAPPING.put(Instructions.Divide.class, DIVIDE);
        MAPPING.put(Instructions.Modulo.class, MODULO);
        MAPPING.put(Instructions.Hash.class, HASH);
        MAPPING.put(Instructions.Jump.class, JUMP);
        MAPPING.put(Instructions.JumpDestination.class, JUMP_DESTINATION);
        MAPPING.put(Instructions.JumpIf.class, JUMP_IF);
        MAPPING.put(Instructions.Load.class, LOAD);
        MAPPING.put(Instructions.Save.class, SAVE);
        MAPPING.put(Instructions.Exit.class, EXIT);
    }

}
