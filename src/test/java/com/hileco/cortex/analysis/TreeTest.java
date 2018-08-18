package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TreeTest {

    @Test
    public void test() {
        Tree tree = new Tree();
        tree.include(Arrays.asList(
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));
        Assert.assertEquals(3, tree.getTreeBlocks().size());
    }
}