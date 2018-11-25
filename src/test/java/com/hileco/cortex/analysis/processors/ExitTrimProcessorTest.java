package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.documentation.Documentation;
import org.junit.Test;

public class ExitTrimProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        Documentation.of(ExitTrimProcessor.class.getSimpleName())
                .headingParagraph(ExitTrimProcessor.class.getSimpleName())
                .paragraph("Removes any instructions following another instruction that guarantees the instructions will not be reached.");
        // Note; this would likely be CALL_RETURN, EXIT, HALT and JUMP
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new ExitTrimProcessor();
    }
}