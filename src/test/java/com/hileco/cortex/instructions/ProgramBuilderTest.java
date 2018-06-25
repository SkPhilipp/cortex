package com.hileco.cortex.instructions;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class ProgramBuilderTest {

    @Test
    public void testJump() throws ProgramException {
        List<Instruction> build = new ProgramBuilderFactory().builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .PUSH(BigInteger.valueOf(10L).toByteArray())
                .JUMP_IF()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .JUMP_DESTINATION()
                .build();
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(build);
        Assert.assertEquals(build.size(), programContext.getInstructionPosition());
        Assert.assertEquals(6, programContext.getInstructionsExecuted());
    }

    @Test
    public void testNoJump() throws ProgramException {
        List<Instruction> build = new ProgramBuilderFactory().builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{124})
                .EQUALS()
                .PUSH(BigInteger.valueOf(10L).toByteArray())
                .JUMP_IF()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .JUMP_DESTINATION()
                .build();
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(build);
        Assert.assertEquals(build.size(), programContext.getInstructionPosition());
        Assert.assertEquals(build.size(), programContext.getInstructionsExecuted());
    }

    @Test
    public void testLoop() throws ProgramException {
        List<Instruction> build = new ProgramBuilderFactory().builder()
                .PUSH(new byte[]{0})
                .JUMP_DESTINATION()
                .PUSH(new byte[]{1})
                .ADD()
                .DUPLICATE(0)
                .PUSH(new byte[]{1, 0})
                .EQUALS()
                .IS_ZERO()
                .PUSH(BigInteger.valueOf(1L).toByteArray())
                .JUMP_IF()
                .build();
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(build);
        Assert.assertEquals(build.size(), programContext.getInstructionPosition());
        Assert.assertEquals(((build.size() - 1) * 256) + 1, programContext.getInstructionsExecuted());
    }

}
