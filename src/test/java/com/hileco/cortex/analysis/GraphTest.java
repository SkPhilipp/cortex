package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GraphTest {

    @Test
    public void test() {
        var graph = new Graph(List.of(
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));
        Assert.assertEquals(3, graph.getGraphBlocks().size());
    }
}