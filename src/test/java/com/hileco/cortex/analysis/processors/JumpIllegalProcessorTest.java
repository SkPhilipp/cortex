package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;

public class JumpIllegalProcessorTest extends ProcessorFuzzTest {

    @Test
    public void testprocessJumpToIllegalInstruction() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpIllegalProcessor()
        ));
        var original = List.of(
                new PUSH(BigInteger.ZERO.toByteArray()),
                new JUMP(),
                new PUSH(BigInteger.ONE.toByteArray())
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();

        Documentation.of(JumpIllegalProcessor.class.getSimpleName())
                .headingParagraph(JumpIllegalProcessor.class.getSimpleName())
                .paragraph("Replaces JUMP instructions with HALTs when they are known to always jump to non-JUMP_DESTINATION instructions or" +
                                   " when they are known to jump out of bounds.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);

        Assert.assertEquals(instructions, List.of(
                new PUSH(BigInteger.ZERO.toByteArray()),
                new HALT(JUMP_TO_ILLEGAL_INSTRUCTION),
                new PUSH(BigInteger.ONE.toByteArray())
        ));
    }

    @Test
    public void testProcessJumpOutOfBounds() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpIllegalProcessor()
        ));
        var original = List.of(
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP(),
                new PUSH(BigInteger.ONE.toByteArray())
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();
        Assert.assertEquals(instructions, List.of(
                new PUSH(BigInteger.TEN.toByteArray()),
                new HALT(JUMP_OUT_OF_BOUNDS),
                new PUSH(BigInteger.ONE.toByteArray())
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new JumpIllegalProcessor();
    }
}
