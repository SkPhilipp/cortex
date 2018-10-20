package com.hileco.cortex.constraints;

import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.constraints.expressions.ReferenceMapping;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Solver implements ReferenceMapping {

    private final Map<Expression.Reference, String> referencesForward;
    private final Map<String, Expression.Reference> referencesBackward;

    public Solver() {
        this.referencesForward = new HashMap<>();
        this.referencesBackward = new HashMap<>();
    }

    public Solution solve(Expression expression) {
        var context = new Context();
        var solver = context.mkSolver();
        solver.add((BoolExpr) expression.asZ3Expr(context, this));
        var status = solver.check();
        var model = solver.getModel();
        var constants = Arrays.stream(model.getConstDecls())
                .collect(Collectors.toMap(
                        functionDeclaration -> this.referencesBackward.get(functionDeclaration.getName().toString()),
                        functionDeclaration -> ((IntNum) model.getConstInterp(functionDeclaration)).getInt()));
        return new Solution(constants, status == Status.SATISFIABLE);
    }

    @Override
    public Map<Expression.Reference, String> getReferencesForward() {
        return this.referencesForward;
    }

    @Override
    public Map<String, Expression.Reference> getReferencesBackward() {
        return this.referencesBackward;
    }
}
