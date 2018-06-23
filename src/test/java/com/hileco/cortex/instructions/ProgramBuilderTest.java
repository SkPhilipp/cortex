package com.hileco.cortex.instructions;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ProgramBuilderTest {

    @Test
    public void testJump() throws ProgramException {
        List<Instruction> build = new ProgramBuilder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .JUMP_IF(10)
                .NOOP()
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
        Assert.assertEquals(5, programContext.getInstructionsExecuted());
    }

    @Test
    public void testNoJump() throws ProgramException {
        List<Instruction> build = new ProgramBuilder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{124})
                .EQUALS()
                .JUMP_IF(10)
                .NOOP()
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
        List<Instruction> build = new ProgramBuilder()
                .PUSH(new byte[]{0})
                .JUMP_DESTINATION()
                .PUSH(new byte[]{1})
                .ADD()
                .DUPLICATE(0)
                .PUSH(new byte[]{1, 0})
                .EQUALS()
                .IS_ZERO()
                .JUMP_IF(1)
                .build();
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(build);
        Assert.assertEquals(build.size(), programContext.getInstructionPosition());
        Assert.assertEquals(((build.size() - 1) * 256) + 1, programContext.getInstructionsExecuted());
    }

}
