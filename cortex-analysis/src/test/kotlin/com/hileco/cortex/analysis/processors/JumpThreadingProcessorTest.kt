package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramException.Reason.WINNER
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert.assertEquals
import org.junit.Test

class JumpThreadingProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                PUSH(5),
                JUMP(),
                JUMP_DESTINATION(),
                PUSH(1234)
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        assertEquals(instructions, listOf(
                PUSH(5),
                JUMP(),
                JUMP_DESTINATION(),
                PUSH(5),
                JUMP(),
                JUMP_DESTINATION(),
                PUSH(1234)
        ))
    }

    @Test
    fun processInfiniteLoop() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                JUMP_DESTINATION(),
                PUSH(0),
                JUMP()
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        assertEquals(instructions, original)
    }

    @Test
    fun processHalt() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                HALT(WINNER)
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        assertEquals(instructions, listOf(
                NOOP(),
                HALT(WINNER),
                JUMP_DESTINATION(),
                HALT(WINNER)
        ))
    }

    @Test
    fun processExit() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                EXIT()
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        assertEquals(instructions, listOf(
                NOOP(),
                EXIT(),
                JUMP_DESTINATION(),
                EXIT()
        ))
    }

    @Test
    fun processImplicitExit() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                NOOP()
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        assertEquals(instructions, listOf(
                NOOP(),
                EXIT(),
                JUMP_DESTINATION(),
                NOOP()
        ))
    }

    @Test
    fun processConditionalJump() {
        val graphBuilder = GraphBuilder(listOf(
                JumpThreadingProcessor()
        ))
        val original = listOf(
                PUSH(1),
                PUSH(3),
                JUMP_IF(),
                JUMP_DESTINATION(),
                PUSH(6),
                JUMP(),
                JUMP_DESTINATION()
        )

        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(JumpThreadingProcessor::class.java.simpleName)
                .headingParagraph(JumpThreadingProcessor::class.java.simpleName)
                .paragraph("Finds JUMP and JUMP_IF instructions whose addresses are blocks that immediately JUMP again." +
                        " When this is the case the address of the first JUMP or JUMP_IF is replaced with the address of the second JUMP." +
                        " Additionally, any JUMPs to blocks which immediately or implicitly EXIT, or HALT will be replaced with that respective instruction.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        assertEquals(instructions, listOf(
                PUSH(1),
                PUSH(6),
                JUMP_IF(),
                JUMP_DESTINATION(),
                NOOP(),
                EXIT(),
                JUMP_DESTINATION()
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpThreadingProcessor()
    }
}
