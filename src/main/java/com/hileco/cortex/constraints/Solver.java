package com.hileco.cortex.constraints;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Solver {

    private final Map<Reference, String> referencesForward;
    private final Map<String, Reference> referencesBackward;

    public Solver() {
        this.referencesForward = new HashMap<>();
        this.referencesBackward = new HashMap<>();
    }

    public Solution solve(Expression expression) {
        var context = new Context();
        var solver = context.mkSolver();
        solver.add((BoolExpr) this.build(context, expression));
        var status = solver.check();
        var model = solver.getModel();
        var constants = Arrays.stream(model.getConstDecls())
                .collect(Collectors.toMap(
                        functionDeclaration -> this.referencesBackward.get(functionDeclaration.getName().toString()),
                        functionDeclaration -> ((IntNum) model.getConstInterp(functionDeclaration)).getInt()));
        return new Solution(constants, status == Status.SATISFIABLE);
    }

    private Expr build(Context context, Expression expression) {
        switch (expression.getType()) {
            case ADD:
                return context.mkAdd(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case SUBTRACT:
                return context.mkSub(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case MULTIPLY:
                return context.mkMul(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case DIVIDE:
                return context.mkDiv(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case LESS_THAN:
                return context.mkLt(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case GREATER_THAN:
                return context.mkGt(
                        (ArithExpr) this.build(context, expression.getLeft()),
                        (ArithExpr) this.build(context, expression.getRight()));
            case EQUAL_TO:
                return context.mkEq(
                        this.build(context, expression.getLeft()),
                        this.build(context, expression.getRight()));
            case NOT_EQUAL_TO:
                return context.mkNot(context.mkEq(
                        this.build(context, expression.getLeft()),
                        this.build(context, expression.getRight())));
            case OR:
                return context.mkOr(
                        (BoolExpr) this.build(context, expression.getLeft()),
                        (BoolExpr) this.build(context, expression.getRight()));
            case AND:
                return context.mkAnd(
                        (BoolExpr) this.build(context, expression.getLeft()),
                        (BoolExpr) this.build(context, expression.getRight()));
            case MODULO:
                return context.mkMod(
                        (IntExpr) this.build(context, expression.getLeft()),
                        (IntExpr) this.build(context, expression.getRight()));
            case REFERENCE:
                var reference = this.referencesForward.computeIfAbsent(expression.getReference(), unmappedReference -> {
                    var key = Integer.toString(this.referencesForward.size());
                    this.referencesBackward.put(key, unmappedReference);
                    return key;
                });
                var referenceSymbol = context.mkSymbol(reference);
                return context.mkIntConst(referenceSymbol);
            case VALUE:
                return context.mkInt(expression.getConstant());
            default:
                throw new IllegalStateException(String.format("%s is not implemented.", expression.getType()));
        }
    }
}
