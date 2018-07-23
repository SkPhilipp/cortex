package com.hileco.cortex.optimizerlow;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramDataSource;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
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
        Program program = programBuilderFactory.builder()
                .PUSH(BigInteger.valueOf(1234L).toByteArray())
                .LOAD(ProgramStoreZone.MEMORY)
                .build();
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
        Program program = programBuilderFactory.builder()
                .PUSH(new byte[]{1})
                .PUSH(new byte[]{3})
                .JUMP_IF()
                .JUMP_DESTINATION()
                .build();
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
        Program original = programBuilderFactory.builder()
                .PUSH(new byte[]{12})
                .PUSH(new byte[]{12})
                .EQUALS()
                .PUSH(new byte[]{13})
                .GREATER_THAN()
                .PUSH(new byte[]{11})
                .LESS_THAN()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{1})
                .PUSH(new byte[]{2})
                .PUSH(new byte[]{2})
                .PUSH(new byte[]{2})
                .PUSH(new byte[]{3})
                .EQUALS()
                .PUSH(new byte[]{123})
                .BITWISE_NOT()
                .BITWISE_AND()
                .BITWISE_XOR()
                .EQUALS()
                .POP()
                .PUSH(new byte[]{2})
                .PUSH(new byte[]{2})
                .BITWISE_OR()
                .PUSH(new byte[]{2})
                .MULTIPLY()
                .PUSH(new byte[]{2})
                .DIVIDE()
                .PUSH(new byte[]{2})
                .PUSH(new byte[]{2})
                .MODULO()
                .SUBTRACT()
                .POP()
                .POP()
                .SWAP(0, 1)
                .POP()
                .EXIT()
                .SAVE(ProgramStoreZone.MEMORY)
                .HASH("SHA3")
                .build();

        ProgramContext programContextForOriginal = new ProgramContext(original);
        ProcessContext processContextForOriginal = new ProcessContext(programContextForOriginal);
        ProgramRunner programRunnerForOriginal = new ProgramRunner(processContextForOriginal);
        programRunnerForOriginal.run();

        Program optimized = new Program();
        optimized.setInstructions(instructionsOptimizer.optimize(programBuilderFactory, original.getInstructions()));
        ProgramContext programContextForOptimized = new ProgramContext(optimized);
        ProcessContext processContextForOptimized = new ProcessContext(programContextForOptimized);
        ProgramRunner programRunnerForOptimized = new ProgramRunner(processContextForOptimized);
        programRunnerForOptimized.run();

        Assert.assertTrue(Arrays.deepEquals(programContextForOriginal.getStack().toArray(), programContextForOptimized.getStack().toArray()));
    }
}
