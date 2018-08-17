package com.hileco.cortex.tree;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.tree.strategies.IfBlockProcessor;
import com.hileco.cortex.tree.strategies.JumpDestinationProcessor;
import com.hileco.cortex.tree.strategies.LoadKnownProgramDataOptimizingProcessor;
import com.hileco.cortex.tree.strategies.LoopBlockProcessor;
import com.hileco.cortex.tree.strategies.ParameterLineProcessor;
import com.hileco.cortex.tree.strategies.ParameterProcessor;
import com.hileco.cortex.tree.strategies.PrecalculateSelfContainedOptimizingProcessor;
import com.hileco.cortex.tree.strategies.PushJumpIfOptimizingProcessor;
import com.hileco.cortex.tree.strategies.VariableProcessor;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramTreeBuilderFuzzTest {
    private ProgramTreeBuilder programTreeBuilder;

    @Before
    public void setup() {
        List<ProgramTreeProcessor> strategies = new ArrayList<>();
        strategies.add(new JumpDestinationProcessor());
        strategies.add(new ParameterProcessor());
        strategies.add(new ParameterLineProcessor());
        strategies.add(new VariableProcessor());
        strategies.add(new IfBlockProcessor());
        strategies.add(new LoopBlockProcessor());

        strategies.add(new LoadKnownProgramDataOptimizingProcessor(new HashMap<>(), new HashSet<>()));
        strategies.add(new PrecalculateSelfContainedOptimizingProcessor());
        strategies.add(new PushJumpIfOptimizingProcessor());
        programTreeBuilder = new ProgramTreeBuilder(strategies);
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
            ProgramTree programTree = programTreeBuilder.build(instructions);
            System.out.println(programTree);
            System.out.flush();
        }
    }
}
