package com.hileco.cortex.demo;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.fuzzer.ProgramGenerator;

import java.util.ArrayList;
import java.util.List;

public class FuzzerTreeDemo {

    private static final long SEED = 0L;

    public static void main(String[] args) {
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        var treeBuilder = new TreeBuilder(processors);
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var tree = treeBuilder.build(program.getInstructions());
        System.out.println(program);
        System.out.println(tree);
    }
}
