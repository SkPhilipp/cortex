package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProcessorFuzzTest {

    private static final int LIMIT_RUNS = 500;

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
     * Fuzzes the processor and verifies correctness by executing non-processed and processed versions
     * of generated programs, comparing for any differences in call results, storage, or transfers.
     */
    private void fuzzTestProcessor(Processor processor) {
        TreeBuilder treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor(),
                new JumpTableProcessor(),
                processor
        ));

        long seed = System.currentTimeMillis() * LIMIT_RUNS;
        long runs = 0;
        while (runs++ < LIMIT_RUNS) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            long runSeed = seed + runs;
            LayeredMap<BigInteger, Program> generated = programGenerator.generate(runSeed);
            LayeredMap<BigInteger, Program> generatedOptimized = programGenerator.generate(runSeed);
            for (BigInteger address : generatedOptimized.keySet()) {
                Program program = generatedOptimized.get(address);
                List<Instruction> instructions = program.getInstructions();
                List<Instruction> optimizedInstructions = treeBuilder.build(instructions).toInstructions();
                generatedOptimized.put(program.getAddress(), new Program(program.getAddress(), optimizedInstructions));
            }
            ProgramContext callerContext = this.executeAll(generated);
            ProgramContext callerContextOptimized = this.executeAll(generatedOptimized);
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
    }

    @Test
    public void fuzzTestExitTrimProcessor() {
        this.fuzzTestProcessor(new ExitTrimProcessor());
    }

    @Test
    public void fuzzTestJumpIllegalProcessor() {
        this.fuzzTestProcessor(new JumpIllegalProcessor());
    }

    @Test
    public void fuzzTestJumpThreadingProcessor() {
        this.fuzzTestProcessor(new JumpThreadingProcessor());
    }

    @Test
    public void fuzzTestJumpUnreachableProcessor() {
        this.fuzzTestProcessor(new JumpUnreachableProcessor());
    }

    @Test
    public void fuzzTestKnownJumpIfProcessor() {
        this.fuzzTestProcessor(new KnownJumpIfProcessor());
    }

    @Test
    public void fuzzTestKnownLoadProcessor() {
        this.fuzzTestProcessor(new KnownLoadProcessor(new HashMap<>(), new HashSet<>()));
    }

    @Test
    public void fuzzTestKnownProcessor() {
        this.fuzzTestProcessor(new KnownProcessor());
    }
}
