package com.hileco.cortex.demo;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.JumpTableProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.mapping.TreeMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class TreeMapperDemo {

    private static final long SEED = 0L;

    public static void main(String[] args) {
        var treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor(),
                new JumpTableProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var tree = treeBuilder.build(program.getInstructions());
        var treeMapper = new TreeMapper();
        var treeMapping = treeMapper.map(tree);
        System.out.println(program);
        System.out.println(tree);
        treeMapping.getJumpMapping().forEach((source, targets) -> {
            System.out.println(String.format("%04d", source));
            targets.forEach(target -> System.out.println(String.format(" --> %04d", target)));
        });
    }
}
