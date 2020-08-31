package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.ReferenceMapping
import com.hileco.cortex.vm.bytes.BackedInteger
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
            val constants = model.constDecls.associateBy(
                    { referencesBackward[it.name.toString()]!! },
                    { BackedInteger((model.getConstInterp(it) as BitVecNum).bigInteger.toByteArray()) }
            )
            Solution(values = constants, solvable = status == Status.SATISFIABLE, condition = expression)
        } catch (e: Z3Exception) {
            // TODO: Handle Z3 exceptions
            e.printStackTrace()
            Solution(condition = expression)
        }
    }
}
