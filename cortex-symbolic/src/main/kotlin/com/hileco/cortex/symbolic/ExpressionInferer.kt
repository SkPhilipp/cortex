package com.hileco.cortex.symbolic

class ExpressionInferer {

    // with an expression like..
    // CALL_DATA[0] == 1234

    // we can infer
    // CALL_DATA[0] == 1234
    // CALL_DATA[0] = 1234

    // with an expression like..
    // CALL_DATA[0] * 2 == 1234

    // we could infer
    // CALL_DATA[0] * 2 == 1234
    // CALL_DATA[0] == 1234 / 2
    // CALL_DATA[0] = 617

    // the flow would be as such

    // 0) the expressions are always pre-optimized
    // 1) find whether the expression contains a subexpression that allows us to infer a value
    //    examples:
    //        CALL_DATA[0] == 1234 && CALL_DATA[1] != 12345
    //        (32 >> CALL_DATA[0]) == 1234 && CALL_DATA[1] != 12345
    //    counterexamples:
    //        CALL_DATA[0] == 1234 || CALL_DATA[0] != 12345
    //      > CALL_DATA[0] == 1234 is such a subexpression however it is part of || meaning nothing can be inferred
    //      !(CALL_DATA[0] == 1234)
    //      > CALL_DATA[0] == 1234 is such a subexpression however it is part of ! meaning nothing can be inferred
    //    we can find such expressions by looking for == expressions where both sides reference exactly 1 variable
    //    and they are not part of logical not or logical or

    // 2) once such an inference subexpression has been identified
    //    we can then rearrange the subexpression so that only the variable is on one side, for simplicity the variable is moved left
    //    example:
    //    total expression:           1234 == (5 + CALL_DATA[0]) && CALL_DATA[1] != 12345
    //    inference subexpression:    1234 == (5 + CALL_DATA[0])
    //    '' repositioned:            (5 + CALL_DATA[0]) == 1234
    //
    //    then, we can look at whether the expression allows to be rearranged as such that we end with `VARIABLE == expression`
    //    at this point we can choose whether or not to ignore changes caused by expression rearranging, as certain nuances such as
    //    integer overflow would cause an expression such as `(CALL_DATA[0] + 1) * 100 == 100` to behave differently from `CALL_DATA[0] == (100 / 100) - 1`
    //    '' repositioned:            (5 + CALL_DATA[0]) == 1234
    //    '' rearranged:              CALL_DATA[0] == (1234 - 5)
    //    once the expression on the right contains only subexpressions referencing values, it can simply be optimized to one value expression
    //    '' rearranged:              CALL_DATA[0] == (1234 - 5)
    //    '' optimized:               CALL_DATA[0] == 1229
    //    if optimization to a single value was successful, we now have an inference rule where the variable on the left can be set to the value on the right
    //    this inference could be stored on a symbolic virtual machine, and its contents can be reevaluated based on the newly known inference

}