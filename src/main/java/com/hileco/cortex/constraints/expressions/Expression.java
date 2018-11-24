package com.hileco.cortex.constraints.expressions;

import com.hileco.cortex.vm.ProgramStoreZone;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Expression {
    Expr asZ3Expr(Context context, ReferenceMapping referenceMapping);

    @FunctionalInterface
    interface InputConverter {
        Expr convert(Context context, Expr input);
    }

    @FunctionalInterface
    interface LeftRightConverter {
        Expr convert(Context context, Expr left, Expr right);
    }

    @Data
    @AllArgsConstructor
    class Input implements Expression {
        private String representation;
        private InputConverter converter;
        private Expression input;

        public static Function<ExpressionStack, Expression> builder(String representation, InputConverter converter) {
            return expressionStack -> {
                var input = expressionStack.pop(0);
                return new Input(representation, converter, input);
            };
        }

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            var inputExpr = this.input.asZ3Expr(context, referenceMapping);
            return this.converter.convert(context, inputExpr);
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", this.representation, this.input);
        }
    }

    @Data
    @AllArgsConstructor
    class LeftRight implements Expression {
        private String representation;
        private LeftRightConverter converter;
        private Expression left;
        private Expression right;

        public static Function<ExpressionStack, Expression> builder(String representation, LeftRightConverter converter) {
            return expressionStack -> {
                var left = expressionStack.pop(0);
                var right = expressionStack.pop(0);
                return new LeftRight(representation, converter, left, right);
            };
        }

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            var leftExpr = this.left.asZ3Expr(context, referenceMapping);
            var rightExpr = this.right.asZ3Expr(context, referenceMapping);
            return this.converter.convert(context, leftExpr, rightExpr);
        }

        @Override
        public String toString() {
            return String.format("(%s %s %s)", this.left, this.representation, this.right);
        }
    }

    @Data
    @AllArgsConstructor
    class Reference implements Expression {
        private ProgramStoreZone type;
        private Expression address;

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            var reference = referenceMapping.getReferencesForward().computeIfAbsent(Reference.this, unmappedReference -> {
                var key = Integer.toString(referenceMapping.getReferencesForward().size());
                referenceMapping.getReferencesBackward().put(key, unmappedReference);
                return key;
            });
            var referenceSymbol = context.mkSymbol(reference);
            return context.mkIntConst(referenceSymbol);
        }

        @Override
        public String toString() {
            return String.format("%s[%s]", this.type, this.address);
        }
    }

    @Data
    @AllArgsConstructor
    class Value implements Expression {
        private Long constant;

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            return context.mkInt(this.constant);
        }

        @Override
        public String toString() {
            return Long.toString(this.constant);
        }
    }

    @Data
    @AllArgsConstructor
    class Stack implements Expression {
        private int address;

        @Override
        public String toString() {
            return String.format("STACK[%d]", this.address);
        }

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            throw new IllegalArgumentException(String.format("Missing stack: %d.", this.address));
        }
    }

    @Data
    @AllArgsConstructor
    class Not implements Expression {
        private Expression input;

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            var inputExpr = this.input.asZ3Expr(context, referenceMapping);
            return context.mkNot((BoolExpr) inputExpr);
        }

        @Override
        public String toString() {
            return String.format("!(%s)", this.input);
        }
    }

    @Data
    @AllArgsConstructor
    class And implements Expression {
        private List<Expression> inputs;

        @Override
        public Expr asZ3Expr(Context context, ReferenceMapping referenceMapping) {
            return context.mkAnd((BoolExpr[]) this.inputs.stream()
                    .map(input -> input.asZ3Expr(context, referenceMapping))
                    .toArray(BoolExpr[]::new));
        }

        @Override
        public String toString() {
            return this.inputs.stream().map(input -> String.format("(%s)", input.toString())).collect(Collectors.joining(" && "));
        }
    }
}
