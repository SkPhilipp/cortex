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

    private static final int LIMIT_RUNS = 10_000;
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
        while (runs++ < LIMIT_RUNS) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            LayeredMap<BigInteger, Program> generated = programGenerator.generate(seed + runs);
            LayeredMap<BigInteger, Program> generatedOptimized = programGenerator.generate(seed + runs);
            for (BigInteger address : generatedOptimized.keySet()) {
                Program program = generatedOptimized.get(address);
                List<Instruction> instructions = program.getInstructions();
                List<Instruction> optimizedInstructions = instructionsOptimizer.optimize(programBuilderFactory, instructions);
                generatedOptimized.put(program.getAddress(), new Program(program.getAddress(), optimizedInstructions));
            }

            ProgramContext callerContext = executeAll(generated);
            ProgramContext callerContextOptimized = executeAll(generatedOptimized);

            Assert.assertEquals(String.format("Issue with Generation %d in caller", seed + runs), callerContext.getMemory(),
                    callerContextOptimized.getMemory());
            Assert.assertEquals(String.format("Issue with Generation %d in caller", seed + runs), callerContext.getProgram().getStorage(),
                    callerContextOptimized.getProgram().getStorage());
            Assert.assertEquals(String.format("Issue with Generation %d in caller", seed + runs), callerContext.getProgram().getTransfers(),
                    callerContextOptimized.getProgram().getTransfers());
            for (BigInteger address : generated.keySet()) {
                Assert.assertEquals(String.format("Issue with Generation %d in program %s", seed + runs, address.toString()),
                        generated.get(address).getStorage(),
                        generatedOptimized.get(address).getStorage());
                Assert.assertEquals(String.format("Issue with Generation %d in program %s", seed + runs, address.toString()),
                        generated.get(address).getTransfers(),
                        generatedOptimized.get(address).getTransfers());
            }
        }
    }
}
