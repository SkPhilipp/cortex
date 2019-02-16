package com.hileco.cortex.constraints.expressions

import com.hileco.cortex.vm.ProgramStoreZone
import com.microsoft.z3.BoolExpr
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import java.lang.Long.toString

interface Expression {
    fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr

    data class Input(val representation: String,
                     val converter: (Context, Expr) -> Expr,
                     val input: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return converter(context, inputExpr)
        }

        override fun toString(): String {
            return "$representation($input)"
        }
    }

    data class LeftRight(val representation: String,
                         val converter: (Context, Expr, Expr) -> Expr,
                         val left: Expression,
                         val right: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return converter(context, leftExpr, rightExpr)
        }

        override fun toString(): String {
            return "($left $representation $right)"
        }
    }

    data class Reference(val type: ProgramStoreZone,
                         val address: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val reference = referenceMapping.referencesForward.computeIfAbsent(this@Reference) { unmappedReference ->
                val key = Integer.toString(referenceMapping.referencesForward.size)
                referenceMapping.referencesBackward[key] = unmappedReference
                key
            }
            val referenceSymbol = context.mkSymbol(reference)
            return context.mkIntConst(referenceSymbol)
        }

        override fun toString(): String {
            return "$type[$address]"
        }
    }

    data class Value(val constant: Long) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkInt(constant)
        }

        override fun toString(): String {
            return toString(constant)
        }
    }

    data class Stack(val address: Int = 0) : Expression {

        override fun toString(): String {
            return "STACK[$address]"
        }

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            throw IllegalArgumentException("Missing stack: $address.")
        }
    }

    data class Not(val input: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return context.mkNot(inputExpr as BoolExpr)
        }

        override fun toString(): String {
            return "!($input)"
        }
    }

    data class And(var inputs: List<Expression>) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkAnd(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun toString(): String {
            return inputs.joinToString(separator = " && ") { "($it)" }
        }
    }

    object True : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkTrue()
        }

        override fun toString(): String {
            return "TRUE"
        }
    }
}
