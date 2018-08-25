package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;

import java.math.BigInteger;
import java.util.function.Consumer;

public class KnownJumpIfProcessor implements Processor {
    private void fully(TreeNode treeNode, Consumer<TreeNode> consumer) {
        consumer.accept(treeNode);
        for (TreeNode parameter : treeNode.getParameters()) {
            this.fully(parameter, consumer);
        }
    }

    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(treeNode -> treeNode.isInstruction(JUMP_IF.class))
                .filter(treeNode -> treeNode.hasParameter(0, TreeNode::isSelfContained))
                .filter(treeNode -> treeNode.hasParameter(1, TreeNode::isSelfContained))
                .forEach(jumpNode -> {
                    TreeNode decidingNode = jumpNode.getParameters().get(1);
                    Program program = new Program(BigInteger.ZERO, decidingNode.toInstructions());
                    ProgramContext programContext = new ProgramContext(program);
                    ProcessContext processContext = new ProcessContext(programContext);
                    ProgramRunner programRunner = new ProgramRunner(processContext);
                    try {
                        programRunner.run();
                    } catch (ProgramException e) {
                        throw new IllegalStateException("Unknown cause for ProgramException", e);
                    }
                    byte[] result = programContext.getStack().peek();
                    if (new BigInteger(result).compareTo(BigInteger.ZERO) > 0) {
                        this.fully(decidingNode, node -> node.getInstruction().set(new NOOP()));
                        jumpNode.getInstruction().set(new JUMP());
                    } else {
                        this.fully(jumpNode, node -> node.getInstruction().set(new NOOP()));
                    }
                }));
    }
}
