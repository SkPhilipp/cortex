package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Graph;

public class JumpIllegalProcessor implements Processor {
    @Override
    public void process(Graph graph) {
        // TODO: replace JumpingInstructions which point outside of the program with HALT
    }
}
