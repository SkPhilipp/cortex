package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class KnownProcessor implements Processor {

    private void noopDownwards(TreeNode treeNode) {
        treeNode.getInstruction().set(new NOOP());
        treeNode.getParameters().forEach(this::noopDownwards);
    }

    @Override
    public void process(Tree tree) {
        // TODO: Parameters could also be selfContained.
        tree.getTreeBlocks().forEach(treeBlock -> treeBlock.getTreeNodes().stream()
                .filter(TreeNode::isSelfContained)
                .forEach(treeNode -> {
                    var program = new Program(BigInteger.ZERO, treeNode.toInstructions());
                    var programContext = new ProgramContext(program);
                    var processContext = new ProcessContext(programContext);
                    var programRunner = new ProgramRunner(processContext);
                    try {
                        programRunner.run();
                    } catch (ProgramException e) {
                        throw new IllegalStateException("Unknown cause for ProgramException", e);
                    }
                    var stack = programContext.getStack();
                    var instructions = stack.stream()
                            .map(PUSH::new)
                            .collect(Collectors.toList());
                    if (instructions.size() == 1) {
                        treeNode.getInstruction().set(instructions.get(0));
                        treeNode.getParameters().forEach(this::noopDownwards);
                    }
                    // TODO: Replace the entire treeNode also when more instructions are available...
                }));
    }
}
