package com.hileco.cortex.instructions;

import com.hileco.cortex.tree.TreeBranch;
import com.hileco.cortex.tree.building.TreeBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TreeBuilderTest {

    @Test
    public void test() {
        TreeBuilder treeBuilder = new TreeBuilder();
        List<Instruction> instructions = new ProgramBuilder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{124})
                .EQUALS()
                .JUMP_IF(10)
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .JUMP_DESTINATION()
                .build();
        TreeBranch root = treeBuilder.asTree(instructions);
        Assert.assertEquals(instructions.size(), root.getBranches().size());
    }
}
