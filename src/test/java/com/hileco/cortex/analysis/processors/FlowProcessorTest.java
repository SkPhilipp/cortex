package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.documentation.Documentation;
import org.junit.Test;

public class FlowProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        Documentation.of(FlowProcessor.class.getSimpleName())
                .headingParagraph(FlowProcessor.class.getSimpleName())
                .paragraph("Adds edges describing the program flow, this includes JUMP and JUMP_IFs where jump address information is known ahead of time.");
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new FlowProcessor();
    }
}