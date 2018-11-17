package com.hileco.cortex.fuzzer;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.Program;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;

public class ProgramGeneratorFuzzTest {

    private static final int LIMIT_RUNS = 100_000;

    @Test
    public void fuzzTestGenerator() {
        var seed = System.currentTimeMillis() * LIMIT_RUNS;
        long exceptions = 0;
        long runs = 0;
        while (runs < LIMIT_RUNS) {
            var programGenerator = new ProgramGenerator();
            var generated = programGenerator.generate(seed + runs);
            var programAddresses = generated.keySet();
            for (var programAddress : programAddresses) {
                runs++;
                try {
                    var caller = new Program(BigInteger.ZERO, new ArrayList<>());
                    var program = generated.get(programAddress);
                    var callerContext = new ProgramContext(caller);
                    var programContext = new ProgramContext(program);
                    var processContext = new VirtualMachine(callerContext, programContext);
                    var programRunner = new ProgramRunner(processContext);
                    programRunner.run();
                } catch (ProgramException e) {
                    exceptions++;
                }
            }
        }
        var ratio = ((double) exceptions) / ((double) runs);
        Assert.assertFalse(String.format("Too many ProgramExceptions per generated program: %f (seed: %d)", ratio, seed), ratio > 0.5);
    }
}
