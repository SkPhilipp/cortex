package com.hileco.cortex.server.api.demo;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoFuzzerApi implements Route {

    private static final String PARAM_SEED = "seed";

    @Override
    public Object handle(Request request, Response response) {
        var seed = Long.parseLong(request.queryParams(PARAM_SEED));
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        var treeBuilder = new TreeBuilder(processors);
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var tree = treeBuilder.build(program.getInstructions());
        return Map.of("program", program.toString(),
                      "tree", tree.toString());
    }
}
