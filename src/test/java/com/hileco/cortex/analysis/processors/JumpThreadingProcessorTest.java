package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.documentation.Documentation;
import org.junit.Test;

public class JumpThreadingProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        Documentation.of(JumpThreadingProcessor.class.getSimpleName())
                .headingParagraph(JumpThreadingProcessor.class.getSimpleName())
                .paragraph("Finds JUMP and JUMP_IF instructions whose addresses are blocks that immediately JUMP again. When this is the case the address of " +
                                   "the first JUMP or JUMP_IF is replaced with the address of the second JUMP");
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new JumpThreadingProcessor();
    }
}
