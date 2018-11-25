package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.documentation.Documentation;
import org.junit.Test;

public class ExitTrimProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        Documentation.of(ExitTrimProcessor.class.getSimpleName())
                .headingParagraph(ExitTrimProcessor.class.getSimpleName())
                .paragraph("Removes any instructions which follow an EXIT, HALT or CALL_RETURN which cannot otherwise be reached.");
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new ExitTrimProcessor();
    }
}