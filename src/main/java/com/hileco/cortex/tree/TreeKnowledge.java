package com.hileco.cortex.tree;

public class TreeKnowledge {

    //    |    operation     |   read   |  write   |
    //    |------------------|----------|----------|
    //    | PUSH             |          |        0 |
    //    | POP              |        1 |          |
    //    | SWAP             |   *1, *2 |   *1, *2 |
    //    | DUPLICATE        |       *1 |        0 |
    //    | BITWISE_OR       |        2 |        0 |
    //    | BITWISE_XOR      |        2 |        0 |
    //    | BITWISE_AND      |        2 |        0 |
    //    | BITWISE_NOT      |        2 |        0 |
    //    | HASH             |        1 |        0 |
    //    | EQUALS           |        2 |        0 |
    //    | GREATER_THAN     |        2 |        0 |
    //    | LESS_THAN        |        2 |        0 |
    //    | IS_ZERO          |        1 |        0 |
    //    | ADD              |        2 |        0 |
    //    | SUBTRACT         |        2 |        0 |
    //    | MULTIPLY         |        2 |        0 |
    //    | DIVIDE           |        2 |        0 |
    //    | MODULO           |        1 |        0 |
    //    | JUMP             |        1 |          |
    //    | JUMP_DESTINATION |          |          |
    //    | NOOP             |          |          |
    //    | JUMP_IF          |        2 |          |
    //    | EXIT             |          |          |
    //    | SAVE             |        2 |          |
    //    | LOAD             |        1 |        1 |

}
