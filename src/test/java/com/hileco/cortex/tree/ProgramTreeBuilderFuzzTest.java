package com.hileco.cortex.tree;

import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.tree.strategies.IfBlockStrategy;
import com.hileco.cortex.tree.strategies.JumpDestinationStrategy;
import com.hileco.cortex.tree.strategies.LoopBlockStrategy;
import com.hileco.cortex.tree.strategies.ParameterLineStrategy;
import com.hileco.cortex.tree.strategies.ParameterStrategy;
import com.hileco.cortex.tree.strategies.VariableStrategy;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProgramTreeBuilderFuzzTest {
    private ProgramTreeBuilder programTreeBuilder;

    @Before
    public void setup() {
        List<ProgramTreeBuildingStrategy> strategies = new ArrayList<>();
        strategies.add(new JumpDestinationStrategy());
        strategies.add(new ParameterStrategy());
        strategies.add(new ParameterLineStrategy());
        strategies.add(new VariableStrategy());
        strategies.add(new IfBlockStrategy());
        strategies.add(new LoopBlockStrategy());
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
        }
    }
}
