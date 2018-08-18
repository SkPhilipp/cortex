package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.processors.JumpTableProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeBuilderFuzzTest {
    private TreeBuilder treeBuilder;

    @Before
    public void setup() {
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        processors.add(new JumpTableProcessor());
        processors.add(new KnownLoadProcessor(new HashMap<>(), new HashSet<>()));
        processors.add(new KnownProcessor());
        processors.add(new KnownJumpIfProcessor());
        treeBuilder = new TreeBuilder(processors);
    }

    @Test
    public void fuzzTestBuilder() {
        long seed = System.currentTimeMillis();
        ProgramGenerator programGenerator = new ProgramGenerator();
        LayeredMap<BigInteger, Program> generated = programGenerator.generate(seed);
        Set<BigInteger> programAddresses = generated.keySet();
        for (BigInteger programAddress : programAddresses) {
            Program program = generated.get(programAddress);
            List<Instruction> instructions = program.getInstructions();
            Tree tree = treeBuilder.build(instructions);
            System.out.println(tree);
            System.out.flush();
        }
    }
}
