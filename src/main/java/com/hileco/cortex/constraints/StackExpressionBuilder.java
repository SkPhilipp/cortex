package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.constraints.expressions.Operation1Expression;
import com.hileco.cortex.constraints.expressions.Operation2Expression;
import com.hileco.cortex.constraints.expressions.ReferenceExpression;
import com.hileco.cortex.constraints.expressions.StackExpression;
import com.hileco.cortex.constraints.expressions.ValueExpression;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.math.HASH;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.PUSH;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class StackExpressionBuilder {

    private static final Map<Class<? extends Instruction>, Operation2Expression.Type2> TYPE_2_MAP;
    private static final Map<Class<? extends Instruction>, Operation1Expression.Type1> TYPE_1_MAP;

    static {
        TYPE_2_MAP = new HashMap<>();
        TYPE_2_MAP.put(BITWISE_OR.class, Operation2Expression.Type2.OR);
        TYPE_2_MAP.put(BITWISE_AND.class, Operation2Expression.Type2.AND);
        // TODO: BITWISE_NOT
        // TODO: BITWISE_XOR
        TYPE_2_MAP.put(ADD.class, Operation2Expression.Type2.ADD);
        TYPE_2_MAP.put(SUBTRACT.class, Operation2Expression.Type2.SUBTRACT);
        TYPE_2_MAP.put(MULTIPLY.class, Operation2Expression.Type2.MULTIPLY);
        TYPE_2_MAP.put(DIVIDE.class, Operation2Expression.Type2.DIVIDE);
        TYPE_2_MAP.put(LESS_THAN.class, Operation2Expression.Type2.LESS_THAN);
        TYPE_2_MAP.put(GREATER_THAN.class, Operation2Expression.Type2.GREATER_THAN);
        TYPE_2_MAP.put(EQUALS.class, Operation2Expression.Type2.EQUAL_TO);
        TYPE_2_MAP.put(MODULO.class, Operation2Expression.Type2.MODULO);
        TYPE_1_MAP = new HashMap<>();
        TYPE_1_MAP.put(HASH.class, Operation1Expression.Type1.HASH);
        // TODO: IS_ZERO
        // TODO: DUPLICATE
        // TODO: SWAP
        // TODO: SAVE
        // TODO: CALL
    }

    private final LayeredStack<Expression> stack = new LayeredStack<>();
    private int missing;

    private Expression popExpression() {
        var top = this.stack.pop();
        return top != null ? top : new StackExpression(this.missing++);
    }

    public Expression getCurrentExpression() {
        return this.stack.peek();
    }

    public int getMissing() {
        return this.missing;
    }

    public void addInstruction(Instruction instruction) {
        Class<? extends Instruction> instructionClass = instruction.getClass();
        if (TYPE_2_MAP.containsKey(instructionClass)) {
            this.stack.push(TYPE_2_MAP.get(instructionClass).on(this.popExpression(), this.popExpression()));
        } else if (TYPE_1_MAP.containsKey(instructionClass)) {
            this.stack.push(TYPE_1_MAP.get(instructionClass).of(this.popExpression()));
        } else if (instruction instanceof LOAD) {
            var loadInstruction = (LOAD) instruction;
            this.stack.push(ReferenceExpression.reference(loadInstruction.getProgramStoreZone(), this.popExpression()));
        } else if (instruction instanceof PUSH) {
            var pushInstruction = (PUSH) instruction;
            var value = new BigInteger(pushInstruction.getBytes());
            this.stack.push(ValueExpression.value(value.longValue()));
        } else {
            instruction.getStackParameters().forEach(parameter -> this.popExpression());
        }
    }
}
