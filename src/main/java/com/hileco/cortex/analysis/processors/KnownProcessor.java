package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class KnownProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        // TODO: Parameters could also be selfContained.
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(TreeNode::isSelfContained)
                .forEach(treeNode -> {
                    Program program = new Program(BigInteger.ZERO, treeNode.toInstructions());
                    ProgramContext programContext = new ProgramContext(program);
                    ProcessContext processContext = new ProcessContext(programContext);
                    ProgramRunner programRunner = new ProgramRunner(processContext);
                    try {
                        programRunner.run();
                    } catch (ProgramException e) {
                        throw new IllegalStateException("Unknown cause for ProgramException", e);
                    }
                    LayeredStack<byte[]> stack = programContext.getStack();
                    List<Instruction> instructions = new ArrayList<>();
                    for (byte[] bytes : stack) {
                        instructions.add(new PUSH(bytes));
                    }
                    // TODO: Replace the entire treeNode...
                }));
    }
}
