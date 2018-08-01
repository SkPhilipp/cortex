package com.hileco.cortex.fuzzer;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Set;

public class ProgramGeneratorFuzzTest {

    // TODO: Seed randoms and put them behind one interface
    // TODO: Ideally one with a java8-like interface to perform a for loop for a random number up to a limit
    // TODO: A program generator context?
    // TODO: Use FuzzExpression to wrap basic function layouts (& remember programbuilder has construct support now)
    // TODO: Give programs always an address, ZERO is fine - remove null|empty constructors & utility methods
    // TODO: Immediately fix any issues that pop up from fuzzing

    @Test
    public void testGenerator() throws InterruptedException {
        while (true) {
            ProgramGenerator programGenerator = new ProgramGenerator();
            LayeredMap<BigInteger, Program> generated = programGenerator.generate();
            Set<BigInteger> programAddresses = generated.keySet();
            programAddresses.forEach(programAddress -> {
                ProgramContext programContext = new ProgramContext(generated.get(programAddress));
                ProcessContext processContext = new ProcessContext(programContext);
                ProgramRunner programRunner = new ProgramRunner(processContext);
                try {
                    programRunner.run();
                } catch (ProgramException e) {
                    e.printStackTrace();
                }
            });
            Thread.sleep(10);
        }
    }
}
