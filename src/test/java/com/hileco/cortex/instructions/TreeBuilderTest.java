package com.hileco.cortex.instructions;

import com.hileco.cortex.tree.TreeBranch;
import com.hileco.cortex.tree.TreeBuilder;
import com.hileco.cortex.tree.building.NoopDownwardStrategy;
import com.hileco.cortex.tree.building.PushJumpIfStrategy;
import com.hileco.cortex.tree.building.PushPopStrategy;
import com.hileco.cortex.tree.building.PushPushConditionStrategy;
import com.hileco.cortex.tree.building.UnusedJumpDestinationStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TreeBuilderTest {

    @Test
    public void testWithoutOptimizations() {
        TreeBuilder treeBuilder = new TreeBuilder();
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory
                .builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
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
        TreeBranch root = treeBuilder.asTree(programBuilderFactory, instructions);
        Assert.assertEquals(instructions.size(), root.getInstructions().size());
    }

    @Test
    public void testPushPopStrategy() {
        TreeBuilder treeBuilder = new TreeBuilder();
        treeBuilder.addStrategy(new PushPopStrategy());
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory
                .builder()
                .PUSH(new byte[]{123})
                .POP()
                .build();
        TreeBranch root = treeBuilder.asTree(programBuilderFactory, instructions);
        Assert.assertTrue(root.getInstructions().get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(1).getOperation() instanceof Operations.NoOp);
    }

    @Test
    public void testPushPushConditionStrategy() {
        TreeBuilder treeBuilder = new TreeBuilder();
        treeBuilder.addStrategy(new PushPushConditionStrategy());
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory
                .builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .build();
        TreeBranch root = treeBuilder.asTree(programBuilderFactory, instructions);
        Assert.assertTrue(root.getInstructions().get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(1).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(2).getOperation() instanceof Operations.Push);
    }

    @Test
    public void testFullOptimization() throws ProgramException {
        TreeBuilder treeBuilder = new TreeBuilder();
        treeBuilder.addStrategy(new PushPopStrategy());
        treeBuilder.addStrategy(new PushPushConditionStrategy());
        treeBuilder.addStrategy(new PushJumpIfStrategy());
        treeBuilder.addStrategy(new UnusedJumpDestinationStrategy());
        treeBuilder.addStrategy(new NoopDownwardStrategy());
        treeBuilder.setPasses(2);
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory
                .builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{124})
                .EQUALS()
                .JUMP_IF(10)
                .NOOP()
                .PUSH(new byte[]{123})
                .NOOP()
                .NOOP()
                .POP()
                .NOOP()
                .JUMP_DESTINATION()
                .build();
        TreeBranch root = treeBuilder.asTree(programBuilderFactory, instructions);
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(root.getInstructions());
    }
}
