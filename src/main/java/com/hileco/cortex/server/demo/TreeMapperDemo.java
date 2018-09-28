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
import com.hileco.cortex.mapping.TreeMapper;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class TreeMapperDemo implements Route {

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
        treeMapping.getJumpMapping().forEach((source, targets) -> {
            stringBuilder.append(String.format("%04d", source));
            stringBuilder.append('\n');
            targets.forEach(target -> {
                stringBuilder.append(String.format(" --> %04d", target));
                stringBuilder.append('\n');

            });
        });
        return stringBuilder.toString();
    }
}
