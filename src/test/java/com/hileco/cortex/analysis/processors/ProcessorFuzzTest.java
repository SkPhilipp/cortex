package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.Program;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProcessorFuzzTest {

    private static final int LIMIT_RUNS = 500;

    private ProgramContext executeAll(LayeredMap<BigInteger, Program> atlas) {
        var caller = new Program(BigInteger.ZERO, new ArrayList<>());
        var callerContext = new ProgramContext(caller);
        for (var programAddress : atlas.keySet()) {
            var program = atlas.get(programAddress);
            var programContext = new ProgramContext(program);
            var processContext = new VirtualMachine(callerContext, programContext);
            try {
                var programRunner = new ProgramRunner(processContext);
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
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                processor
        ));

        var seed = System.currentTimeMillis() * LIMIT_RUNS;
        long runs = 0;
        while (runs++ < LIMIT_RUNS) {
            var programGenerator = new ProgramGenerator();
            var runSeed = seed + runs;
            var generated = programGenerator.generate(runSeed);
            var generatedOptimized = programGenerator.generate(runSeed);
            for (var address : generatedOptimized.keySet()) {
                var program = generatedOptimized.get(address);
                var instructions = program.getInstructions();
                var optimizedInstructions = graphBuilder.build(instructions).toInstructions();
                generatedOptimized.put(program.getAddress(), new Program(program.getAddress(), optimizedInstructions));
            }
            var callerContext = this.executeAll(generated);
            var callerContextOptimized = this.executeAll(generatedOptimized);
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getMemory(),
                                callerContextOptimized.getMemory());
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getProgram().getStorage(),
                                callerContextOptimized.getProgram().getStorage());
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.getProgram().getTransfers(),
                                callerContextOptimized.getProgram().getTransfers());
            for (var address : generated.keySet()) {
                var standard = generated.get(address);
                var optimized = generatedOptimized.get(address);
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
