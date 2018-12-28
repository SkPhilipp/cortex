package com.hileco.cortex.constraints.expressions

import com.hileco.cortex.vm.ProgramStoreZone
import com.microsoft.z3.BoolExpr
import com.microsoft.z3.Context
import com.microsoft.z3.Expr

interface Expression {
    fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr

    data class Input(private var representation: String,
                     private var converter: (Context, Expr) -> Expr,
                     private var input: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return converter(context, inputExpr)
        }

        override fun toString(): String {
            return "$representation($input)"
        }
    }

    data class LeftRight(private var representation: String,
                         private var converter: (Context, Expr, Expr) -> Expr,
                         private var left: Expression,
                         private var right: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return converter(context, leftExpr, rightExpr)
        }

        override fun toString(): String {
            return "($left $representation $right)"
        }
    }

    data class Reference(private var type: ProgramStoreZone,
                         private var address: Expression) : Expression {

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

    data class Value(private var constant: Long) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkInt(constant)
        }

        override fun toString(): String {
            return java.lang.Long.toString(constant)
        }
    }

    data class Stack(private val address: Int = 0) : Expression {

        override fun toString(): String {
            return "STACK[$address]"
        }

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            throw IllegalArgumentException("Missing stack: $address.")
        }
    }

    data class Not(private val input: Expression) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return context.mkNot(inputExpr as BoolExpr)
        }

        override fun toString(): String {
            return "!($input)"
        }
    }

    data class And(private var inputs: List<Expression>) : Expression {

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkAnd(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun toString(): String {
            return inputs.joinToString(separator = " && ") { "($it)" }
        }
    }
}
