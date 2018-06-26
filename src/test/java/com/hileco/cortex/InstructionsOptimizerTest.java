package com.hileco.cortex;

import com.hileco.cortex.data.ProgramData;
import com.hileco.cortex.data.ProgramDataScope;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.instructions.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.tree.TreeBranch;
import com.hileco.cortex.optimizer.InstructionsOptimizer;
import com.hileco.cortex.optimizer.strategies.LoadKnownProgramDataStrategy;
import com.hileco.cortex.optimizer.strategies.PushJumpIfStrategy;
import com.hileco.cortex.optimizer.strategies.PushPopStrategy;
import com.hileco.cortex.optimizer.strategies.PushPushConditionStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class InstructionsOptimizerTest {

    @Test
    public void testWithoutOptimizations() {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .PUSH(BigInteger.valueOf(10L).toByteArray())
                .JUMP_IF()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .NOOP()
                .JUMP_DESTINATION()
                .build();
        TreeBranch root = instructionsOptimizer.asTree(programBuilderFactory, instructions);
        Assert.assertEquals(instructions.size(), root.getInstructions().size());
    }

    @Test
    public void testPushPopStrategy() {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PushPopStrategy());
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(new byte[]{123})
                .POP()
                .build();
        TreeBranch root = instructionsOptimizer.asTree(programBuilderFactory, instructions);
        Assert.assertTrue(root.getInstructions().get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(1).getOperation() instanceof Operations.NoOp);
    }

    @Test
    public void testPushPushConditionStrategy() {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PushPushConditionStrategy());
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{123})
                .EQUALS()
                .build();
        TreeBranch root = instructionsOptimizer.asTree(programBuilderFactory, instructions);
        Assert.assertTrue(root.getInstructions().get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(1).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(2).getOperation() instanceof Operations.Push);
    }

    @Test
    public void testLoadKnownProgramDataStrategy() {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        Map<String, Map<BigInteger, ProgramData>> knownData = new HashMap<>();
        Map<BigInteger, ProgramData> knownGroup = new HashMap<>();
        knownGroup.put(BigInteger.valueOf(1234L), new ProgramData(new byte[]{123}, ProgramDataScope.DEPLOYMENT));
        knownData.put("KNOWN", knownGroup);
        LoadKnownProgramDataStrategy strategy = new LoadKnownProgramDataStrategy(knownData, new HashSet<>(Collections.singletonList(ProgramDataScope.DEPLOYMENT)));
        instructionsOptimizer.addStrategy(strategy);
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(BigInteger.valueOf(1234L).toByteArray())
                .LOAD("KNOWN")
                .build();
        TreeBranch root = instructionsOptimizer.asTree(programBuilderFactory, instructions);
        Assert.assertTrue(root.getInstructions().get(0).getOperation() instanceof Operations.NoOp);
        Assert.assertTrue(root.getInstructions().get(1).getOperation() instanceof Operations.Push);
        Assert.assertTrue(root.getInstructions().get(1).getOperands() instanceof Operations.Push.Operands);
        Assert.assertArrayEquals(((Operations.Push.Operands) root.getInstructions().get(1).getOperands()).bytes, new byte[]{123});
    }

    @Test
    public void testFullOptimization() throws ProgramException {
        InstructionsOptimizer instructionsOptimizer = new InstructionsOptimizer();
        instructionsOptimizer.addStrategy(new PushPopStrategy());
        instructionsOptimizer.addStrategy(new PushPushConditionStrategy());
        instructionsOptimizer.addStrategy(new PushJumpIfStrategy());
        instructionsOptimizer.setPasses(2);
        ProgramBuilderFactory programBuilderFactory = new ProgramBuilderFactory();
        List<Instruction> instructions = programBuilderFactory.builder()
                .PUSH(new byte[]{123})
                .PUSH(new byte[]{124})
                .EQUALS()
                .PUSH(BigInteger.valueOf(10L).toByteArray())
                .JUMP_IF()
                .NOOP()
                .PUSH(new byte[]{123})
                .NOOP()
                .NOOP()
                .POP()
                .JUMP_DESTINATION()
                .build();
        TreeBranch root = instructionsOptimizer.asTree(programBuilderFactory, instructions);
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        programRunner.run(root.getInstructions());
    }
}
