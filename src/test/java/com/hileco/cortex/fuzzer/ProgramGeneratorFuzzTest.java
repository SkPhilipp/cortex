package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

public class ProgramGeneratorFuzzTest {

    private static final int LIMIT_RUNS = 100_000;

    @Test
    public void fuzzTestGenerator() {
        long seed = System.currentTimeMillis() * LIMIT_RUNS;
        long exceptions = 0;
        long runs = 0;
        while (runs < LIMIT_RUNS) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            LayeredMap<BigInteger, Program> generated = programGenerator.generate(seed + runs);
            Set<BigInteger> programAddresses = generated.keySet();
            for (BigInteger programAddress : programAddresses) {
                runs++;
                try {
                    Program caller = new Program(BigInteger.ZERO, new ArrayList<>());
                    Program program = generated.get(programAddress);
                    ProgramContext callerContext = new ProgramContext(caller);
                    ProgramContext programContext = new ProgramContext(program);
                    ProcessContext processContext = new ProcessContext(callerContext, programContext);
                    ProgramRunner programRunner = new ProgramRunner(processContext);
                    programRunner.run();
                } catch (ProgramException e) {
                    exceptions++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        double ratio = ((double) exceptions) / ((double) runs);
        Assert.assertFalse(String.format("Too many ProgramExceptions per generated program: %f", ratio), ratio > 0.5);
    }
}
