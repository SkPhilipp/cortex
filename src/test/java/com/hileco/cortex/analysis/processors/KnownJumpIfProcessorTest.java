package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeBuilder;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnownJumpIfProcessorTest {

    @Test
    public void testProcess() {
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        processors.add(new JumpTableProcessor());
        processors.add(new KnownJumpIfProcessor());
        TreeBuilder treeBuilder = new TreeBuilder(processors);

        Tree processed = treeBuilder.build(Arrays.asList(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        ));

        Tree expected = treeBuilder.build(Arrays.asList(
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP()
          ));

        // TODO: Perform assertions
        System.out.println(processed);
        System.out.println(expected);
    }
}