package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuildingStrategy;

public class BaseStrategy implements ProgramTreeBuildingStrategy  {
    @Override
    public void expand(ProgramTree programTree) {
        // TODO: Implement basic instruction to program node conversion
        // - introduces node type "instruction"
        //- introduces node type "jump-destination"
    }
}
