package com.hileco.cortex.tree.strategies;

import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramTree;
import com.hileco.cortex.tree.ProgramTreeBuildingStrategy;

import java.util.List;

import static com.hileco.cortex.instructions.Operations.Duplicate;
import static com.hileco.cortex.instructions.Operations.Swap;
import static com.hileco.cortex.tree.ProgramNodeType.INSTRUCTION;

public class ParameterStrategy implements ProgramTreeBuildingStrategy {
    @Override
    public void expand(ProgramTree programTree) {
        LayeredStack<ProgramNode> stack = new LayeredStack<>();
        List<ProgramNode> programNodes = programTree.getNodes();
        for (int node = 0; node < programNodes.size(); node++) {
            ProgramNode programNode = programNodes.get(node);
            Instruction<?, ?> instruction = programNode.getInstruction();
            if (programNode.getType() != INSTRUCTION
                    || (instruction.getOperation() instanceof Duplicate)
                    || (instruction.getOperation() instanceof Swap)) {
                stack.clear();
                continue;
            }
            List<Integer> stackTakes = instruction.getStackTakes();
            if (stackTakes.size() > 0 && stack.size() >= stackTakes.size()) {
                for (int i = 0; i < stackTakes.size(); i++) {
                    ProgramNode parameter = stack.pop();
                    programNode.getParameters().add(parameter);
                    programNodes.remove(parameter);
                    node--;
                }
            }
            if (instruction.getStackAdds().size() > 0) {
                stack.push(programNode);
            }
        }
    }
}
