package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.Tree;
import com.hileco.cortex.analysis.TreeNode;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.SWAP;

import java.util.List;

import static com.hileco.cortex.analysis.TreeNodeType.INSTRUCTION;
import static com.hileco.cortex.analysis.TreeNodeType.UNKNOWN;

public class ParameterProcessor implements Processor {
    public void process(Tree tree) {
        tree.getTreeBlocks().forEach(treeBlock -> {
            LayeredStack<TreeNode> stack = new LayeredStack<>();
            List<TreeNode> treeNodes = treeBlock.getTreeNodes();
            for (int node = 0; node < treeNodes.size(); node++) {
                TreeNode treeNode = treeNodes.get(node);
                Instruction instruction = treeNode.getInstruction().get();
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
                List<Integer> stackTakes = instruction.getStackTakes();
                int limit = stack.size();
                for (int i = 0; i < stackTakes.size(); i++) {
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
                if (instruction.getStackAdds().size() > 0) {
                    stack.push(treeNode);
                }
            }
        });
    }
}
