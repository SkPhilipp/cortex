package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.ReferenceMapping
import com.microsoft.z3.*
import java.util.*

class Solver : ReferenceMapping {
    override val referencesForward: MutableMap<Expression.Reference, String> = HashMap()
    override val referencesBackward: MutableMap<String, Expression.Reference> = HashMap()
    override val hashFunctions: MutableMap<String, FuncDecl> = HashMap()

    fun solve(expression: Expression): Solution {
        return try {
            val context = Context()
            val solver = context.mkSolver()
            solver.add(expression.asZ3Expr(context, this) as BoolExpr)
            val status = solver.check()
            val model = solver.model
            val constants = model.constDecls.associateBy({ referencesBackward[it.name.toString()]!! }, { (model.getConstInterp(it) as IntNum).int64 })
            Solution(constants, status == Status.SATISFIABLE)
        } catch (e: Z3Exception) {
            Solution(HashMap(), false)
        }
    }
}