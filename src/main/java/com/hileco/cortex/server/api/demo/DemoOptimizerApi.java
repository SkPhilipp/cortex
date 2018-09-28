package com.hileco.cortex.server.api.demo;

import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.JumpTableProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.stack.PUSH;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DemoOptimizerApi implements Route {

    @Override
    public Object handle(Request request, Response response) {
        var treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor()
        ));
        var optimizedTreeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor(),
                new JumpTableProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var program = new Program(Arrays.asList(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        var tree = treeBuilder.build(program.getInstructions());
        var optimizedTree = optimizedTreeBuilder.build(program.getInstructions());
        return Map.of("program", program.toString(),
                      "tree", tree.toString(),
                      "optimizedTree", optimizedTree.toString());
    }
}
