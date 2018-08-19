package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

public class InstructionsBuilderTest {

    @Test
    public void testJump() throws ProgramException {
        Program program = new Program(BigInteger.ZERO, Arrays.asList(
                new PUSH(new byte[]{123}),
                new PUSH(new byte[]{123}),
                new EQUALS(),
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new JUMP_IF(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new JUMP_DESTINATION()
        ));
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(6, programContext.getInstructionsExecuted());
    }

    @Test
    public void testNoJump() throws ProgramException {
        Program program = new Program(BigInteger.ZERO, Arrays.asList(
                new PUSH(new byte[]{123}),
                new PUSH(new byte[]{124}),
                new EQUALS(),
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new JUMP_IF(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new NOOP(),
                new JUMP_DESTINATION()
        ));
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionsExecuted());
    }

    @Test
    public void testLoop() throws ProgramException {
        Program program = new Program(BigInteger.ZERO, Arrays.asList(
                new PUSH(new byte[]{0}),
                new JUMP_DESTINATION(),
                new PUSH(new byte[]{1}),
                new ADD(),
                new DUPLICATE(0),
                new PUSH(new byte[]{1, 0}),
                new EQUALS(),
                new IS_ZERO(),
                new PUSH(BigInteger.valueOf(1L).toByteArray()),
                new JUMP_IF()
        ));
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(((program.getInstructions().size() - 1) * 256) + 1, programContext.getInstructionsExecuted());
    }

}