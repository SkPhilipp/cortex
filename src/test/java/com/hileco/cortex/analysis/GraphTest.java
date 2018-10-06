package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class GraphTest {

    @Test
    public void test() {
        var graph = new Graph(Arrays.asList(
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));
        Assert.assertEquals(3, graph.getGraphBlocks().size());
    }
}