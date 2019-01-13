package com.hileco.cortex.analysis.processors

import com.hileco.cortex.documentation.Documentation
import org.junit.Ignore
import org.junit.Test

@Ignore
class JumpThreadingProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        Documentation.of(JumpThreadingProcessor::class.simpleName!!)
                .headingParagraph(JumpThreadingProcessor::class.simpleName!!)
                .paragraph("Finds JUMP and JUMP_IF instructions whose addresses are blocks that immediately JUMP again. When this is the case the address of " + "the first JUMP or JUMP_IF is replaced with the address of the second JUMP")
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpThreadingProcessor()
    }
}
