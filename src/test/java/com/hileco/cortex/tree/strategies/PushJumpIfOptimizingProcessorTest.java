package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuilder;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PushJumpIfOptimizingProcessorTest {

    @Test
    public void testProcess() {
        List<ProgramTreeProcessor> strategies = new ArrayList<>();
        strategies.add(new ParameterProcessor());
        strategies.add(new PushJumpIfOptimizingProcessor());
        ProgramTreeBuilder programTreeBuilder = new ProgramTreeBuilder(strategies);

        ProgramBuilder builder = new ProgramBuilder();
        builder.PUSH(BigInteger.ONE.toByteArray());
        builder.PUSH(BigInteger.TEN.toByteArray());
        builder.JUMP_IF();
        Program program = builder.build();
        ProgramTree programTree = programTreeBuilder.build(program.getInstructions());

        ProgramBuilder expectedBuilder = new ProgramBuilder();
        expectedBuilder.NOOP();
        expectedBuilder.PUSH(BigInteger.TEN.toByteArray());
        expectedBuilder.JUMP();

        Program expectedProgram = expectedBuilder.build();
        ProgramTree expectedProgramTree = programTreeBuilder.build(expectedProgram.getInstructions());

        System.out.println(programTree);
        System.out.println(expectedProgramTree);
    }
}