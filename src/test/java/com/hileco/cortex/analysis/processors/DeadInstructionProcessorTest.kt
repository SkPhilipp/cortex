package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.DISK
import org.junit.Assert
import org.junit.Test

class DeadInstructionProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                DeadInstructionProcessor()
        ))
        val original = listOf(
                PUSH(10),
                PUSH(1),
                HALT(WINNER)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadInstructionProcessor::class.java.simpleName)
                .headingParagraph(DeadInstructionProcessor::class.java.simpleName)
                .paragraph("Removes instructions before a HALT or EXIT in the same block, which do not perform any kind of permanent modification.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(NOOP(), NOOP(), HALT(WINNER)))
    }

    @Test
    fun processImplicitExit() {
        val graphBuilder = GraphBuilder(listOf(
                DeadInstructionProcessor()
        ))
        val original = listOf(
                PUSH(10),
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(NOOP(), NOOP()))
    }

    @Test
    fun processExplicitExitWithDiskChange() {
        val graphBuilder = GraphBuilder(listOf(
                DeadInstructionProcessor()
        ))
        val original = listOf(
                PUSH(10),
                PUSH(1),
                SAVE(DISK),
                HALT(WINNER)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, original)
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadInstructionProcessor()
    }
}