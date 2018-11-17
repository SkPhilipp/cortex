package com.hileco.cortex.attack;

import com.hileco.cortex.analysis.edges.EdgeFlow;
import com.hileco.cortex.analysis.edges.EdgeFlowType;
import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hileco.cortex.analysis.edges.EdgeFlowType.BLOCK_END;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.BLOCK_PART;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.END;
import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP_IF;

@Value
public class AttackPath {
    private static final Set<EdgeFlowType> BLOCK_TO_END_TYPES = Set.of(BLOCK_PART, BLOCK_END, END);

    private List<Instruction> instructions;
    private List<EdgeFlow> edgeFlows;

    public Expression toExpression() {
        var expressionGenerator = new ExpressionGenerator();
        var conditions = new ArrayList<Expression>();
        for (var i = 0; i < this.edgeFlows.size(); i++) {
            var edgeFlow = this.edgeFlows.get(i);
            if (BLOCK_TO_END_TYPES.contains(edgeFlow.getType())) {
                var edgeFlowNextAvailable = i + 1 < this.edgeFlows.size();
                var source = edgeFlow.getSource();
                var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(this.instructions.size() - 1);
                for (var j = source; j <= target; j++) {
                    var instruction = this.instructions.get(j);
                    if (instruction instanceof JUMP_IF) {
                        var isLastOfBlock = target.equals(j);
                        var isNextBlockJumpIf = edgeFlowNextAvailable && INSTRUCTION_JUMP_IF == this.edgeFlows.get(i + 1).getType();
                        var expression = expressionGenerator.viewExpression(JUMP_IF.CONDITION.getPosition());
                        if (!(isLastOfBlock && isNextBlockJumpIf)) {
                            expression = new Expression.Not(expression);
                        }
                        conditions.add(expression);
                    }
                    expressionGenerator.addInstruction(instruction);
                }
            }
        }
        return new Expression.And(conditions);
    }
}
