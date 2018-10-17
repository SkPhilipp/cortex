package com.hileco.cortex.constraints.expressions;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Operation2Expression implements Expression {

    @FunctionalInterface
    private interface Converter {
        Expr convert(Context context, Expr left, Expr right);
    }

    public enum Type2 {
        ADD("+", (context, left, right) -> context.mkAdd((ArithExpr) left, (ArithExpr) right)),
        SUBTRACT("-", (context, left, right) -> context.mkSub((ArithExpr) left, (ArithExpr) right)),
        MULTIPLY("*", (context, left, right) -> context.mkMul((ArithExpr) left, (ArithExpr) right)),
        DIVIDE("/", (context, left, right) -> context.mkDiv((ArithExpr) left, (ArithExpr) right)),
        LESS_THAN("<", (context, left, right) -> context.mkLt((ArithExpr) left, (ArithExpr) right)),
        GREATER_THAN(">", (context, left, right) -> context.mkGt((ArithExpr) left, (ArithExpr) right)),
        EQUAL_TO("==", Context::mkEq),
        NOT_EQUAL_TO("!=", (context, left, right) -> context.mkNot(context.mkEq(left, right))),
        OR("||", (context, left, right) -> context.mkOr((BoolExpr) left, (BoolExpr) right)),
        AND("&&", (context, left, right) -> context.mkAnd((BoolExpr) left, (BoolExpr) right)),
        MODULO("%", (context, left, right) -> context.mkMod((IntExpr) left, (IntExpr) right));

        private final String representation;
        private final Converter converter;

        Type2(String representation, Converter converter) {
            this.representation = representation;
            this.converter = converter;
        }

        @Override
        public String toString() {
            return this.representation;
        }

        public Operation2Expression on(Expression left, Expression right) {
            return new Operation2Expression(this, left, right);
        }
    }

    private Type2 type;
    private Expression left;
    private Expression right;

    @Override
    public Expr build(Context context, ReferenceMapping referenceMapping) {
        return this.type.converter.convert(context,
                                           this.left.build(context, referenceMapping),
                                           this.right.build(context, referenceMapping));
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", this.left, this.type, this.right);
    }
}
