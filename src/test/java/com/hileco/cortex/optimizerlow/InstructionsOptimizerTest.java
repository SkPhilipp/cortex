package com.hileco.cortex.optimizerlow;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.optimizerlow.strategies.LoadKnownProgramDataStrategy;
import com.hileco.cortex.optimizerlow.strategies.PrecalculateSelfContainedStrategy;
import com.hileco.cortex.optimizerlow.strategies.PushJumpIfStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class InstructionsOptimizerTest {

    @Test
    public void testLoadKnownProgramDataStrategy() {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        Map<ProgramStoreZone, Map<BigInteger, ProgramData>> knownData = new HashMap<>();
        Map<BigInteger, ProgramData> knownGroup = new HashMap<>();
        knownGroup.put(BigInteger.valueOf(1234L), new ProgramData(new byte[]{123}, new HashSet<>(Collections.singleton(ProgramDataSource.FIXED))));
        knownData.put(ProgramStoreZone.MEMORY, knownGroup);
        LoadKnownProgramDataStrategy strategy = new LoadKnownProgramDataStrategy(knownData, new HashSet<>(Collections.singletonList(ProgramDataSource.FIXED)));
        instructionsOptimizer.addStrategy(strategy);
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        ProgramBuilder builder = programBuilderFactory.builder();
        builder.PUSH(BigInteger.valueOf(1234L).toByteArray());
        builder.LOAD(ProgramStoreZone.MEMORY);
        Program program = builder.build();
        List<Instruction> optimized = instructionsOptimizer.optimize(programBuilderFactory, program.getInstructions());
        Assert.assertTrue(optimized.get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(optimized.get(1).getOperation() instanceof Operations.Push);
    }

    @Test
    public void testPushJumpIfStrategy() throws ProgramException {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PushJumpIfStrategy());
        instructionsOptimizer.setPasses(2);
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        ProgramBuilder builder = programBuilderFactory.builder();
        builder.PUSH(new byte[]{1});
        builder.PUSH(new byte[]{3});
        builder.JUMP_IF();
        builder.JUMP_DESTINATION();
        Program program = builder.build();
        List<Instruction> optimized = instructionsOptimizer.optimize(programBuilderFactory, program.getInstructions());
        ProgramContext programContext = new ProgramContext(program);
        ProcessContext processContext = new ProcessContext(programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        Assert.assertTrue(optimized.get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(optimized.get(1).getOperation() instanceof Operations.Push);
    }

    @Test
    public void testPrecalculateSelfContainedStrategy() throws ProgramException {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PrecalculateSelfContainedStrategy());
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        ProgramBuilder builder = programBuilderFactory.builder();
        builder.PUSH(new byte[]{12});
        builder.PUSH(new byte[]{12});
        builder.EQUALS();
        builder.PUSH(new byte[]{13});
        builder.GREATER_THAN();
        builder.PUSH(new byte[]{11});
        builder.LESS_THAN();
        builder.PUSH(new byte[]{123});
        builder.PUSH(new byte[]{1});
        builder.PUSH(new byte[]{2});
        builder.PUSH(new byte[]{2});
        builder.PUSH(new byte[]{2});
        builder.PUSH(new byte[]{3});
        builder.EQUALS();
        builder.PUSH(new byte[]{123});
        builder.BITWISE_NOT();
        builder.BITWISE_AND();
        builder.BITWISE_XOR();
        builder.EQUALS();
        builder.POP();
        builder.PUSH(new byte[]{2});
        builder.PUSH(new byte[]{2});
        builder.BITWISE_OR();
        builder.PUSH(new byte[]{2});
        builder.MULTIPLY();
        builder.PUSH(new byte[]{2});
        builder.DIVIDE();
        builder.PUSH(new byte[]{2});
        builder.PUSH(new byte[]{2});
        builder.MODULO();
        builder.SUBTRACT();
        builder.POP();
        builder.POP();
        builder.SWAP(0, 1);
        builder.POP();
        builder.EXIT();
        builder.SAVE(ProgramStoreZone.MEMORY);
        builder.HASH("SHA3");
        Program original = builder.build();

        ProgramContext programContextForOriginal = new ProgramContext(original);
        ProcessContext processContextForOriginal = new ProcessContext(programContextForOriginal);
        ProgramRunner programRunnerForOriginal = new ProgramRunner(processContextForOriginal);
        programRunnerForOriginal.run();

        Program optimized = new Program(instructionsOptimizer.optimize(programBuilderFactory, original.getInstructions()));
        ProgramContext programContextForOptimized = new ProgramContext(optimized);
        ProcessContext processContextForOptimized = new ProcessContext(programContextForOptimized);
        ProgramRunner programRunnerForOptimized = new ProgramRunner(processContextForOptimized);
        programRunnerForOptimized.run();

        Assert.assertTrue(Arrays.deepEquals(programContextForOriginal.getStack().toArray(), programContextForOptimized.getStack().toArray()));
    }
}
