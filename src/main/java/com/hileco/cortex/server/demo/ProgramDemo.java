package com.hileco.cortex.server.demo;

import com.hileco.cortex.analysis.TreeBuilder;
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

public class ProgramDemo implements Route {

    @Override
    public Object handle(Request request, Response response) {
        var treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor()
        ));
        var program = new Program(Arrays.asList(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        var tree = treeBuilder.build(program.getInstructions());
        return String.valueOf(program) + '\n' + tree;
    }
}
