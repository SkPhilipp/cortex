package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.documentation.Documentation;
import org.junit.Test;

public class JumpIllegalProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        Documentation.of(JumpIllegalProcessor.class.getSimpleName())
                .headingParagraph(JumpIllegalProcessor.class.getSimpleName())
                .paragraph("Replaces JUMP and JUMP_IF instructions with NOOPS when they are known to always jump on non-JUMP_DESTINATION instructions or " +
                                   "which jump out of bounds.");
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new JumpIllegalProcessor();
    }
}
