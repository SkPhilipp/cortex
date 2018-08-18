package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuilder;
import com.hileco.cortex.tree.ProgramTreeProcessor;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PushJumpIfOptimizingProcessorTest {

    @Test
    public void testProcess() {
        List<ProgramTreeProcessor> strategies = new ArrayList<>();
        strategies.add(new ParameterProcessor());
        strategies.add(new PushJumpIfOptimizingProcessor());
        ProgramTreeBuilder programTreeBuilder = new ProgramTreeBuilder(strategies);

        ProgramTree programTree = programTreeBuilder.build(Arrays.asList(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        ));

        ProgramTree expectedProgramTree = programTreeBuilder.build(Arrays.asList(
                new NOOP(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP()
        ));

        System.out.println(programTree);
        System.out.println(expectedProgramTree);
    }
}