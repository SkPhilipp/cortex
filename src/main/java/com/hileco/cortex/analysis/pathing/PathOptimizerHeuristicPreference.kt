package com.hileco.cortex.analysis.pathing

//## Optimization: Finding Boundaries
//
//Given the high-level `for(int i = 0; i < 10; i++)` loop, it is immediatley visible what the boundaries are. In this case, the loop will execute 1 to 10 times.
//
//Being able to infer these boundaries through the Cortex Graph would be very befinicial to brute force solvers.
//
//A rudimentary method for this could be to interpret any magic number found near loop code as a possible boundary.
class PathOptimizerHeuristicPreference : PathOptimizer