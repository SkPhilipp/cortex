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

    public static final int LIMIT_RUNS = 50000;

    @Test
    public void testGenerator() {
        double exceptions = 0D;
        double runs = 0D;
        while (exceptions < LIMIT_RUNS) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            LayeredMap<BigInteger, Program> generated = programGenerator.generate();
            Set<BigInteger> programAddresses = generated.keySet();
            for (BigInteger programAddress : programAddresses) {
                try {
                    Program caller = new Program(BigInteger.ZERO, new ArrayList<>());
                    Program program = generated.get(programAddress);
                    ProgramContext callerContext = new ProgramContext(caller);
                    ProgramContext programContext = new ProgramContext(program);
                    ProcessContext processContext = new ProcessContext(callerContext, programContext);
                    ProgramRunner programRunner = new ProgramRunner(processContext);
                    exceptions++;
                    programRunner.run();
                } catch (ProgramException e) {
                    runs++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        double ratio = runs / exceptions;
        Assert.assertFalse("Too many ProgramExceptions per generated program", ratio > 0.5);
    }
}
