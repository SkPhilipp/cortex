package com.hileco.cortex.demo;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.fuzzer.ProgramGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FuzzerTreeDemo {

    private static final long SEED = 0L;

    public static void main(String[] args) {
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        TreeBuilder treeBuilder = new TreeBuilder(processors);
        ProgramGenerator programGenerator = new ProgramGenerator();
        LayeredMap<BigInteger, Program> generated = programGenerator.generate(SEED);
        BigInteger first = generated.keySet().iterator().next();
        Program program = generated.get(first);
        Tree tree = treeBuilder.build(program.getInstructions());
        System.out.println(program);
        System.out.println(tree);
    }
}
