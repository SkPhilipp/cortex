package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramStoreZone.DISK
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.util.*

class KnownLoadProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val configuration = mapOf(DISK to mapOf(ONE_32 to ZERO_32))
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownLoadProcessor(configuration)
        ))
        val original = listOf(
                PUSH(ONE_32),
                LOAD(DISK)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Documentation.of(KnownLoadProcessor::class.java.simpleName)
                .headingParagraph(KnownLoadProcessor::class.java.simpleName)
                .paragraph("Replaces LOAD instructions which are known to always provide the same data.")
                .paragraph("In this example, the processor has been configured with the following data:").source(configuration)
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)
        Assert.assertEquals(instructions, listOf(
                NOOP(),
                PUSH(ZERO_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return KnownLoadProcessor(HashMap())
    }
}