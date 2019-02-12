package com.hileco.cortex.analysis.pathing

//## Optimization: Cost-based Pathing
//
//Certain paths can be eliminated depending on the cost of going through a path. For example:
//
//var counter = 0;
//for(int i = 0; i < 1000000000; i++) {
//    for(int j = 0; j < 1000000000; j++) {
//        counter++;
//    }
//}
//log(counter)
//
//It would be possible to determine ahead of time that running this code would be too expensive, as such attempting
//to actually iterate for the specified amount would be skipped.
//
//Essentially this also imposes an upper limit to instructions in pathing.
class PathOptimizerEliminateLongPaths : PathOptimizer