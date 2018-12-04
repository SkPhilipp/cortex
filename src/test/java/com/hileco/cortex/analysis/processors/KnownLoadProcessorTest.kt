package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.ProgramStoreZone.DISK
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.util.*

class KnownLoadProcessorTest : ProcessorFuzzTest() {

    @Test
    fun process() {
        val configuration = mapOf(DISK to mapOf(BigInteger.ONE to BigInteger.ZERO))
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownLoadProcessor(configuration)
        ))
        val original = listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                LOAD(DISK)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Documentation.of(KnownLoadProcessor::class.simpleName!!)
                .headingParagraph(KnownLoadProcessor::class.simpleName!!)
                .paragraph("Replaces LOAD instructions which are known to always provide the same data.")
                .paragraph("In this example, the processor has been configured with the following data:").source(configuration)
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)
        Assert.assertEquals(instructions, listOf(
                NOOP(),
                PUSH(BigInteger.ZERO.toByteArray())
        ))
    }

    internal override fun fuzzTestableProcessor(): Processor {
        return KnownLoadProcessor(HashMap<ProgramStoreZone, Map<BigInteger, BigInteger>>())
    }
}