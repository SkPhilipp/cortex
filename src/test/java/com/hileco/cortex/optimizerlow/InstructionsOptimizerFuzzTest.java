package com.hileco.cortex.optimizerlow;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.optimizerlow.strategies.PrecalculateSelfContainedStrategy;
import com.hileco.cortex.optimizerlow.strategies.PushJumpIfStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InstructionsOptimizerFuzzTest {

    private static final int LIMIT_RUNS = 5_000;
    private final ProgramBuilderFactory programBuilderFactory;
    private final InstructionsOptimizer instructionsOptimizer;

    public InstructionsOptimizerFuzzTest() {
        programBuilderFactory = new ProgramBuilderFactory();
        instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PrecalculateSelfContainedStrategy());
        instructionsOptimizer.addStrategy(new PushJumpIfStrategy());
    }

    private ProgramContext executeAll(LayeredMap<BigInteger, Program> atlas) {
        Program caller = new Program(BigInteger.ZERO, new ArrayList<>());
        ProgramContext callerContext = new ProgramContext(caller);
        for (BigInteger programAddress : atlas.keySet()) {
            Program program = atlas.get(programAddress);
            ProgramContext programContext = new ProgramContext(program);
            ProcessContext processContext = new ProcessContext(callerContext, programContext);
            try {
                ProgramRunner programRunner = new ProgramRunner(processContext);
                programRunner.run();
            } catch (ProgramException ignored) {
            }
        }
        return callerContext;
    }

    /**
     * Fuzzes the optimizer and verifies correctness by executing non-optimized and optimized versions
     * of generated programs, comparing for any differences in call results, storage, or transfers.
     */
    @Test
    public void fuzzTestOptimizer() {
        long seed = System.currentTimeMillis() * LIMIT_RUNS;
        long runs = 0;
        long totalTimeStandard = 0;
        long totalTimeOptimized = 0;
        while (runs++ < LIMIT_RUNS) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            long runSeed = seed + runs;
            LayeredMap<BigInteger, Program> generated = programGenerator.generate(runSeed);
            LayeredMap<BigInteger, Program> generatedOptimized = programGenerator.generate(runSeed);
            for (BigInteger address : generatedOptimized.keySet()) {
                Program program = generatedOptimized.get(address);
                List<Instruction> instructions = program.getInstructions();
                List<Instruction> optimizedInstructions = instructionsOptimizer.optimize(programBuilderFactory, instructions);
                generatedOptimized.put(program.getAddress(), new Program(program.getAddress(), optimizedInstructions));
            }

            long startStandard = System.nanoTime();
            ProgramContext callerContext = executeAll(generated);
            long endStandard = System.nanoTime();
            if (startStandard < endStandard) {
                totalTimeStandard += endStandard - startStandard;
            }

            long startOptimized = System.nanoTime();
            ProgramContext callerContextOptimized = executeAll(generatedOptimized);
            long endOptimized = System.nanoTime();
            if (startOptimized < endOptimized) {
                totalTimeOptimized += endOptimized - startOptimized;
            }

            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getMemory(),
                    callerContextOptimized.getMemory());
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getProgram().getStorage(),
                    callerContextOptimized.getProgram().getStorage());
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getProgram().getTransfers(),
                    callerContextOptimized.getProgram().getTransfers());
            for (BigInteger address : generated.keySet()) {
                Program standard = generated.get(address);
                Program optimized = generatedOptimized.get(address);
                Assert.assertEquals(String.format("Issue with runSeed %d in program %s", runSeed, address.toString()),
                        standard.getStorage(),
                        optimized.getStorage());
                Assert.assertEquals(String.format("Issue with runSeed %d in program %s", runSeed, address.toString()),
                        standard.getTransfers(),
                        optimized.getTransfers());
            }
        }
        double ratio = ((double) totalTimeOptimized / (double) totalTimeStandard);
        Assert.assertTrue(String.format("Optimized runs took %.0f%% of unoptimized runs' time.  (seed: %d)", ratio * 100, seed), ratio < 0.95);
    }
}
