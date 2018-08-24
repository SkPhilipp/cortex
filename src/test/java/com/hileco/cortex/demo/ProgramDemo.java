package com.hileco.cortex.demo;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.Arrays;

public class ProgramDemo {

    public static void main(String[] args) {
        TreeBuilder treeBuilder = new TreeBuilder(Arrays.asList(
                new ParameterProcessor()
        ));
        Program program = new Program(Arrays.asList(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        Tree tree = treeBuilder.build(program.getInstructions());
        System.out.println(program);
        System.out.println(tree);
    }
}
