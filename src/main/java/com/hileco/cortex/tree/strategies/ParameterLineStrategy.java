package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuildingStrategy;

public class ParameterLineStrategy implements ProgramTreeBuildingStrategy {
    @Override
    public void expand(ProgramTree programTree) {
        // TODO: Implement
        //- introduces field "lines" on node type "reference" indicating the source line(s) of the stack element
        //
        //Sample 3: Expression Strategies & References
        //--------------------------------------------
        //    0.5. push(0x1200)
        //      1. load(call_data)
        //    1.5. push(0x2400)
        //      2. load(call_data)
        //      3. add()
        //      4. dup(0)
        //      5. push(0x5001)
        //      6. equals()
        //      7. push(101)
        //      8. jump_if()
        //      9. push(0x6001)
        //     10. equals()
        //     11. push(201)
        //     12. jump_if()
        //         ...
        //    101. (jump_destination)
        //         ...
        //    201. (jump_destination)
        //
        //[ParameterStrategy]
        //      1. add(load(call_data, push(0x1200)), load(call_data, push(0x2400)))
        //      8. jump_if(101, equals(0x5001, dup(0)))
        //     12. jump_if(201, equals(0x6001, pop()))
        //         ...
        //    101. (jump_destination)
        //         ...
        //    201. (jump_destination)
        //
        //[ParameterStrategy, ParameterLineStrategy]
        //      1. add(load(call_data, push(0x1200)), load(call_data, push(0x2400)))
        //      8. jump_if(101, equals(0x5001, dup(@1)))
        //     12. jump_if(101, equals(0x6001, @1))
        //         ...
        //    101. (jump_destination)
        //         ...
        //    201. (jump_destination)
        //
        //[ParameterStrategy, ParameterLineStrategy, VariableStrategy]
        //      1. var a = add(load(call_data, push(0x1200)), load(call_data, push(0x2400)))
        //      8. jump_if(101, equals(0x5001, @a))
        //     12. jump_if(101, equals(0x6001, @a))
        //         ...
        //    101. (jump_destination)
        //         ...
        //    201. (jump_destination)
    }
}
