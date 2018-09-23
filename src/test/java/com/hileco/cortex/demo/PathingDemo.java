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
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.mapping.TreeMapper;
import com.hileco.cortex.pathing.PathIterator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PathingDemo {

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
        var pathIterator = new PathIterator(treeMapping, 1);
        var instructions = tree.toInstructions();
        pathIterator.forEachRemaining(integers -> {
            System.out.println("        ┌───────────────────────────────────");
            for (var i = 0; i < integers.size() - 1; i++) {
                var current = integers.get(i);
                var next = integers.get(i + 1);
                var instruction = instructions.get(current);
                if (current == 1 || instruction instanceof JUMP_DESTINATION) {
                    System.out.println(String.format("        │ %% %d through %d", current, next));
                    for (int index = current; index <= next; index++) {
                        instruction = instructions.get(index);
                        System.out.println(String.format(" %06d │ %s", index, instruction));
                    }
                }
            }
            var current = integers.get(integers.size() - 1);
            var instruction = instructions.get(current);
            System.out.println("        │ % ending block");
            System.out.println(String.format(" %06d │ %s", current, instruction));
            System.out.println("        └───────────────────────────────────");
        });
    }
}
