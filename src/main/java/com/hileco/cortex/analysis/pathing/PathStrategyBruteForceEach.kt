package com.hileco.cortex.analysis.pathing

//A certain amount of repetitions, targeting a single path's win condition.
//
//Examples:
//Loop 10 times, verify whether the last iteration's HALT(WINNER) path is possible. Outcome: WINNER, but not most efficient
//Loop 00 times, ...                                                                Outcome: No winner
//Loop 01 times, ...                                                                Outcome: No winner
class PathStrategyBruteForceEach : PathStrategy