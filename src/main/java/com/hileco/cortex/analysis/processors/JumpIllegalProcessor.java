package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;

public class JumpIllegalProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        // TODO: replace JumpingInstructions which point outside of the program with HALT
    }
}
