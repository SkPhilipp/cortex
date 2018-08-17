package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;

public class IfBlockProcessor implements ProgramTreeProcessor {
    @Override
    public void process(ProgramTree programTree) {
        // TODO: Implement
        // - introduces node type "if-block"
        //
        //Sample 1: Jump-related strategies
        //---------------------------------
        //      1. load(call_data, 0x1200)
        //      2. load(call_data, 0x2400)
        //      3. equals()
        //      4. push(10)
        //      5. jump_if()
        //         ...
        //     10. (jump_destination)
        //         ...
        //
        //[ParameterProcessor]
        //      5. jump_if(10, equals(load(call_data, 0x1200), load(call_data, 0x2400)))
        //         ...
        //     10. (jump_destination)
        //         ...
        //
        //[ParameterProcessor, IfBlockProcessor]
        //    if(equals(load(call_data, 0x1200), load(call_data, 0x2400))) {
        //        ...
        //    }
        //    ...
    }
}
