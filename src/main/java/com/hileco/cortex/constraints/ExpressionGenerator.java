package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.constraints.expressions.ExpressionStack;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_NOT;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.bits.BITWISE_XOR;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.io.SAVE;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.math.HASH;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.hileco.cortex.constraints.expressions.Expression.Input;
import static com.hileco.cortex.constraints.expressions.Expression.LeftRight;
import static com.hileco.cortex.constraints.expressions.Expression.Reference;
import static com.hileco.cortex.constraints.expressions.Expression.Stack;
import static com.hileco.cortex.constraints.expressions.Expression.Value;

public class ExpressionGenerator implements ExpressionStack {

    private static final Map<Class<? extends Instruction>, Function<ExpressionStack, Expression>> MAP;

    static {
        MAP = new HashMap<>();
        MAP.put(ADD.class, LeftRight.builder("+", (context, left, right) -> context.mkAdd((ArithExpr) left, (ArithExpr) right)));
        MAP.put(SUBTRACT.class, LeftRight.builder("-", (context, left, right) -> context.mkSub((ArithExpr) left, (ArithExpr) right)));
        MAP.put(MULTIPLY.class, LeftRight.builder("*", (context, left, right) -> context.mkMul((ArithExpr) left, (ArithExpr) right)));
        MAP.put(DIVIDE.class, LeftRight.builder("/", (context, left, right) -> context.mkDiv((ArithExpr) left, (ArithExpr) right)));
        MAP.put(LESS_THAN.class, LeftRight.builder("<", (context, left, right) -> context.mkLt((ArithExpr) left, (ArithExpr) right)));
        MAP.put(GREATER_THAN.class, LeftRight.builder(">", (context, left, right) -> context.mkGt((ArithExpr) left, (ArithExpr) right)));
        MAP.put(EQUALS.class, LeftRight.builder("==", Context::mkEq));
        MAP.put(BITWISE_OR.class, LeftRight.builder("||", (context, left, right) -> context.mkOr((BoolExpr) left, (BoolExpr) right)));
        MAP.put(BITWISE_AND.class, LeftRight.builder("&&", (context, left, right) -> context.mkAnd((BoolExpr) left, (BoolExpr) right)));
        MAP.put(MODULO.class, LeftRight.builder("%", (context, left, right) -> context.mkMod((IntExpr) left, (IntExpr) right)));
        MAP.put(IS_ZERO.class, Input.builder("0 == ", (context, input) -> context.mkEq(input, context.mkInt(0))));
        MAP.put(HASH.class, Input.builder("HASH", (context, input) -> {
            throw new UnsupportedOperationException();
        }));
        MAP.put(BITWISE_XOR.class, Input.builder("XOR", (context, input) -> {
            throw new UnsupportedOperationException();
        }));
        MAP.put(BITWISE_NOT.class, Input.builder("NOT", (context, input) -> {
            throw new UnsupportedOperationException();
        }));
        MAP.put(DUPLICATE.class, Input.builder("DUPLICATE", (context, input) -> {
            throw new UnsupportedOperationException();
        }));
        MAP.put(SWAP.class, Input.builder("SWAP", (context, input) -> {
            throw new UnsupportedOperationException();
        }));
        MAP.put(SAVE.class, LeftRight.builder("SAVE", (context, left, right) -> {
            throw new UnsupportedOperationException();
        }));
    }

    private final LayeredStack<Expression> stack = new LayeredStack<>();
    private int missing;

    @Override
    public Expression pop(int index) {
        if (index != 0) {
            throw new UnsupportedOperationException();
        }
        var top = this.stack.pop();
        return top != null ? top : new Stack(this.missing++);
    }

    public Expression getCurrentExpression() {
        return this.stack.peek();
    }

    public Expression viewExpression(int offset) {
        return this.stack.get(this.stack.size() - offset);
    }

    public void addInstruction(Instruction instruction) {
        var instructionClass = instruction.getClass();
        if (MAP.containsKey(instructionClass)) {
            this.stack.push(MAP.get(instructionClass).apply(this));
        } else if (instruction instanceof LOAD) {
            var loadInstruction = (LOAD) instruction;
            this.stack.push(new Reference(loadInstruction.getProgramStoreZone(), this.pop(0)));
        } else if (instruction instanceof PUSH) {
            var pushInstruction = (PUSH) instruction;
            var value = new BigInteger(pushInstruction.getBytes());
            this.stack.push(new Value(value.longValue()));
        } else {
            instruction.getStackParameters().forEach(parameter -> this.pop(0));
        }
    }
}
