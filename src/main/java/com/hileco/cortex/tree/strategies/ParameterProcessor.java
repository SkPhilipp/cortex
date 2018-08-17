package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.SWAP;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeProcessor;

import java.util.List;

import static com.hileco.cortex.tree.ProgramNodeType.INSTRUCTION;
import static com.hileco.cortex.tree.ProgramNodeType.UNKNOWN;

public class ParameterProcessor implements ProgramTreeProcessor {
    @Override
    public void process(ProgramTree programTree) {
        LayeredStack<ProgramNode> stack = new LayeredStack<>();
        List<ProgramNode> programNodes = programTree.getNodes();
        for (int node = 0; node < programNodes.size(); node++) {
            ProgramNode programNode = programNodes.get(node);
            Instruction instruction = programNode.getInstruction();
            if (programNode.getType() != INSTRUCTION
            || instruction instanceof JUMP_DESTINATION
            || instruction instanceof SWAP) {
                stack.clear();
                continue;
            }
            if (instruction instanceof DUPLICATE) {
                stack.clear();
                stack.push(programNode);
                continue;
            }
            List<Integer> stackTakes = instruction.getStackTakes();
            int limit = stack.size();
            for (int i = 0; i < stackTakes.size(); i++) {
                ProgramNode parameter;
                if (i < limit) {
                    parameter = stack.pop();
                    node--;
                } else {
                    parameter = new ProgramNode();
                    parameter.setType(UNKNOWN);
                }
                programNode.getParameters().add(parameter);
                programNodes.remove(parameter);
            }
            if (instruction.getStackAdds().size() > 0) {
                stack.push(programNode);
            }
        }
    }
}
