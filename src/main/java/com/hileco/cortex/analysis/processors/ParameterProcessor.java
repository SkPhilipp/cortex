package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.SWAP;

import static com.hileco.cortex.analysis.TreeNodeType.INSTRUCTION;
import static com.hileco.cortex.analysis.TreeNodeType.UNKNOWN;

public class ParameterProcessor implements Processor {
    @Override
    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> {
            LayeredStack<TreeNode> stack = new LayeredStack<>();
            var treeNodes = treeBlock.getTreeNodes();
            for (var node = 0; node < treeNodes.size(); node++) {
                var treeNode = treeNodes.get(node);
                var instruction = treeNode.getInstruction().get();
                if (treeNode.getType() != INSTRUCTION
                        || instruction instanceof JUMP_DESTINATION
                        || instruction instanceof SWAP) {
                    stack.clear();
                    continue;
                }
                if (instruction instanceof DUPLICATE) {
                    stack.clear();
                    stack.push(treeNode);
                    continue;
                }
                var stackTakes = instruction.getStackTakes();
                var limit = stack.size();
                for (var i = 0; i < stackTakes.size(); i++) {
                    TreeNode parameter;
                    if (i < limit) {
                        parameter = stack.pop();
                        node--;
                    } else {
                        parameter = new TreeNode();
                        parameter.setType(UNKNOWN);
                    }
                    treeNode.getParameters().add(parameter);
                    treeNodes.remove(parameter);
                }
                if (!instruction.getStackAdds().isEmpty()) {
                    stack.push(treeNode);
                }
            }
        });
    }
}
