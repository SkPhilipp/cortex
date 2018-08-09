package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuildingStrategy;

public class LoopBlockStrategy implements ProgramTreeBuildingStrategy  {
    @Override
    public void expand(ProgramTree programTree) {
        // TODO: Implement
        // - introduces node type "loop-block"
        //
        //Sample 2: Loop-related strategies
        //---------------------------------
        //      0. (jump_destination)
        //      1. load(call_data, 0x1200)
        //      2. load(call_data, 0x2400)
        //      3. equals()
        //      4. push(101)
        //      5. jump_if()
        //         ...
        //     99. push()
        //    100. jump()
        //    101. (jump_destination)
        //    ...
        //
        //[ParameterStrategy, LoopBlockStrategy]
        //      5. while(not(equals(load(call_data, 0x1200), load(call_data, 0x2400)))) {
        //             ...
        //         }
        //         ...
    }
}
