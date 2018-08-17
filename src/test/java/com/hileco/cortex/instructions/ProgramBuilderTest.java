package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class ProgramBuilderTest {

    @Test
    public void testJump() throws ProgramException {
        ProgramBuilder builder = new ProgramBuilder();
        builder.PUSH(new byte[]{123});
        builder.PUSH(new byte[]{123});
        builder.EQUALS();
        builder.PUSH(BigInteger.valueOf(10L).toByteArray());
        builder.JUMP_IF();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.JUMP_DESTINATION();
        Program program = builder.build();
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(6, programContext.getInstructionsExecuted());
    }

    @Test
    public void testNoJump() throws ProgramException {
        ProgramBuilder builder = new ProgramBuilder();
        builder.PUSH(new byte[]{123});
        builder.PUSH(new byte[]{124});
        builder.EQUALS();
        builder.PUSH(BigInteger.valueOf(10L).toByteArray());
        builder.JUMP_IF();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.NOOP();
        builder.JUMP_DESTINATION();
        Program program = builder
                .build();
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionsExecuted());
    }

    @Test
    public void testLoop() throws ProgramException {
        ProgramBuilder builder = new ProgramBuilder();
        builder.PUSH(new byte[]{0});
        builder.JUMP_DESTINATION();
        builder.PUSH(new byte[]{1});
        builder.ADD();
        builder.DUPLICATE(0);
        builder.PUSH(new byte[]{1, 0});
        builder.EQUALS();
        builder.IS_ZERO();
        builder.PUSH(BigInteger.valueOf(1L).toByteArray());
        builder.JUMP_IF();
        Program program = builder.build();
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertEquals(program.getInstructions().size(), programContext.getInstructionPosition());
        Assert.assertEquals(((program.getInstructions().size() - 1) * 256) + 1, programContext.getInstructionsExecuted());
    }

}
