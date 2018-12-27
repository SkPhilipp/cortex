package com.hileco.cortex.constraints

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.ReferenceMapping
import com.microsoft.z3.*
import java.util.*
import java.util.stream.Collectors

class Solver : ReferenceMapping {
    override val referencesForward: MutableMap<Expression.Reference, String> = HashMap()
    override val referencesBackward: MutableMap<String, Expression.Reference> = HashMap()

    fun solve(expression: Expression): Solution {
        val context = Context()
        val solver = context.mkSolver()
        solver.add(expression.asZ3Expr(context, this) as BoolExpr)
        val status = solver.check()
        val model = solver.model
        val constants = Arrays.stream(model.constDecls)
                .collect(Collectors.toMap<FuncDecl, Expression.Reference, Long>(
                        { functionDeclaration -> referencesBackward[functionDeclaration.name.toString()] },
                        { functionDeclaration -> (model.getConstInterp(functionDeclaration) as IntNum).int64 }))
        return Solution(constants, status == Status.SATISFIABLE)
    }
}
