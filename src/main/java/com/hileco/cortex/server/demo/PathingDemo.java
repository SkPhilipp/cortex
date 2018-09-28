package com.hileco.cortex.server.demo;

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
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PathingDemo implements Route {

    private static final String SEED = "0";
    private static final String PARAM_SEED = "seed";

    @Override
    public Object handle(Request request, Response response) {
        var seed = Long.parseLong(request.queryParamOrDefault(PARAM_SEED, SEED));
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
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var tree = treeBuilder.build(program.getInstructions());
        var treeMapper = new TreeMapper();
        var treeMapping = treeMapper.map(tree);
        var stringBuilder = new StringBuilder();
        stringBuilder.append(program);
        stringBuilder.append('\n');
        var pathIterator = new PathIterator(treeMapping, 1);
        var instructions = tree.toInstructions();
        pathIterator.forEachRemaining(integers -> {
            stringBuilder.append("        ┌───────────────────────────────────");
            stringBuilder.append('\n');
            for (var i = 0; i < integers.size() - 1; i++) {
                var current = integers.get(i);
                var next = integers.get(i + 1);
                var instruction = instructions.get(current);
                if (current == 1 || instruction instanceof JUMP_DESTINATION) {
                    stringBuilder.append(String.format("        │ %% %d through %d", current, next));
                    stringBuilder.append('\n');
                    for (int index = current; index <= next; index++) {
                        instruction = instructions.get(index);
                        stringBuilder.append(String.format(" %06d │ %s", index, instruction));
                        stringBuilder.append('\n');
                    }
                }
            }
            var current = integers.get(integers.size() - 1);
            var instruction = instructions.get(current);
            stringBuilder.append("        │ % ending block");
            stringBuilder.append('\n');
            stringBuilder.append(String.format(" %06d │ %s", current, instruction));
            stringBuilder.append('\n');
            stringBuilder.append("        └───────────────────────────────────");
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }
}
