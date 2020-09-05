package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.Variable
import com.hileco.cortex.symbolic.expressions.ReferenceMapping
import com.microsoft.z3.*

class Solver {
    /**
     * This ensures the byte array extracted from Z3 is the expected size.
     *
     * In cases where [bytes] is too small, it is padded with zeroes.
     * In cases where [bytes] is too large, it is sliced removing bytes from the start.
     */
    private fun alignSize(bytes: ByteArray, variable: Variable): ByteArray {
        val expected = variable.sizeBits / 8
        when {
            bytes.size < expected -> {
                val result = ByteArray(expected)
                System.arraycopy(bytes, 0, result, expected - bytes.size, bytes.size)
                return result
            }
            bytes.size > expected -> {
                return bytes.sliceArray(IntRange(bytes.size - expected, bytes.size - 1))
            }
            else -> {
                return bytes
            }
        }
    }

    fun solve(expression: Expression): Solution {
        try {
            val context = Context()
            val solver = context.mkSolver()
            val referenceMapping = ReferenceMapping()
            solver.add(expression.asZ3Expr(context, referenceMapping) as BoolExpr)
            val status = solver.check()
            if (status == Status.SATISFIABLE) {
                val model = solver.model
                val constants = model.constDecls.associateBy(
                        { referenceMapping.referencesBackward[it.name.toString()]!! },
                        {
                            val bytes = (model.getConstInterp(it) as BitVecNum).bigInteger.toByteArray()
                            val referencedExpression = referenceMapping.referencesBackward[it.name.toString()]!!
                            if (referencedExpression is Variable) alignSize(bytes, referencedExpression) else bytes
                        }
                )
                return Solution(values = constants, solvable = status == Status.SATISFIABLE, condition = expression)
            }
        } catch (e: Z3Exception) {
            // TODO: Handle Z3 exceptions
            e.printStackTrace()
        }
        return Solution(condition = expression)
    }
}
