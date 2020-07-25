1) "Jumps to non-concrete (-32 >> CALL_DATA[0])"

Expressions should support determining what variables that expression references, and how they are used.

Concept:

    Name Constraint Expression                           Potential Inference
    a    1 < 2                                           []
    b    CALL_DATA[0] == 100                             [ {CALL_DATA[0], 100} ]
    c    MEMORY[CALL_DATA[0]] == 150                     [ {MEMORY[CALL_DATA[0]], 150} ]
    d    (-32 >> CALL_DATA[1]) == -1173636544            [ {(-32 >> CALL_DATA[1]), -1173636544} ]
    d    CALL_DATA[2] > 100                              [ {CALL_DATA[2], 100} ]

From this we may be able to infer what values certain variables _must always be_ when they are used on a constraint path, for example `b` indicates CALL_DATA[0]
 must be 100. As such we can set it to 100 and reevaluate and optimize our other expressions, such as `c`.

Other constraints may be inferred on data that allows checking whether certain paths are even possible, for example a rule as such as `d` indicates a
constraint on the possible range of values CALL_DATA[2] is allowed to be. Meaning when a potential path constraint is encountered where it must be less or equal
 to `100`, we know that path is not possible under current constraints before passing it to a solver.

2) "Transfer to: (-1 && (-1 && CALL_DATA[4]))""

Potentially this is some kind of trick to change byte size? `-1` likely currently indicates `0xffffffff...`

3) "BigInteger out of long range"

This should have overflowed instead at an earlier stage.

4) "Assumed Uniqueness"

For program store zone indexes which are not known ahead of time it may be possible to assume uniqueness of a certain key by its expression.
For example `SHA3(CALL_DATA[0])` can not be known ahead of time, however when no other `SHA3(...)` is used as a key, it is very likely the key is unique.
It may be possible to assume uniqueness of such keys, or further even mark those as a path constraint where possible.

It may also be possible to solve expressions and see whether multiple values are even possible under the current path constraints. For example a key of
expression `CALL_DATA[0]` where another constraint exists that puts `CALL_DATA[0]` in a range of only 1 value, the key could simply be solved instead.

It may also be possible to determine a potential range of an expression to find the range of a key under the current path constraints.
For example a key of  expression `CALL_DATA[0]` where another constraint exists that puts `CALL_DATA[0]` in a range of 1 to 100, the area of memory could be
marked as being potentially addressed by `CALL_DATA[0]`.
