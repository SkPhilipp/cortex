package com.hileco.cortex.symbolic.expressions

import com.hileco.cortex.vm.ProgramStoreZone
import com.microsoft.z3.*

interface Expression {
    fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr

    fun subexpressions(): List<Expression>

    data class Reference(val type: ProgramStoreZone,
                         val address: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val reference = referenceMapping.referencesForward.computeIfAbsent(this@Reference) { unmappedReference ->
                val key = referenceMapping.referencesForward.size.toString()
                referenceMapping.referencesBackward[key] = unmappedReference
                key
            }
            val referenceSymbol = context.mkSymbol(reference)
            return context.mkIntConst(referenceSymbol)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(address)
        }

        override fun toString(): String {
            return "$type[$address]"
        }
    }

    data class Value(val constant: Long) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkInt(constant)
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            return constant.toString()
        }
    }

    data class Stack(val address: Int = 0) : Expression {
        override fun toString(): String {
            return "STACK[$address]"
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
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

        override fun subexpressions(): List<Expression> {
            return listOf(input)
        }

        override fun toString(): String {
            return "!($input)"
        }
    }

    data class And(val inputs: List<Expression>) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkAnd(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun subexpressions(): List<Expression> {
            return inputs
        }

        override fun toString(): String {
            return inputs.joinToString(separator = " && ", prefix = "(", postfix = ")") { "$it" }
        }
    }

    data class Or(val inputs: List<Expression>) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkOr(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun subexpressions(): List<Expression> {
            return inputs
        }

        override fun toString(): String {
            return inputs.joinToString(separator = " || ", prefix = "(", postfix = ")") { "$it" }
        }
    }

    object True : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkTrue()
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            return "TRUE"
        }
    }

    object False : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkFalse()
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            return "FALSE"
        }
    }

    data class Add(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkAdd(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left + $right)"
        }
    }

    data class Subtract(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkSub(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left - $right)"
        }
    }

    data class Multiply(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkMul(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left * $right)"
        }
    }

    data class Divide(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkDiv(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left / $right)"
        }
    }

    data class LessThan(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkLt(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left < $right)"
        }
    }

    data class GreaterThan(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkGt(leftExpr as ArithExpr, rightExpr as ArithExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left > $right)"
        }
    }

    data class Equals(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkEq(leftExpr, rightExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left == $right)"
        }
    }

    data class BitwiseOr(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkOr(leftExpr as BoolExpr, rightExpr as BoolExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left || $right)"
        }
    }

    data class BitwiseAnd(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkAnd(leftExpr as BoolExpr, rightExpr as BoolExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left && $right)"
        }
    }

    data class ShiftRight(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVASHR(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left >> $right)"
        }
    }

    data class Modulo(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkMod(leftExpr as IntExpr, rightExpr as IntExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left % $right)"
        }
    }

    data class Exponent(val left: Expression, val right: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkPower(leftExpr as IntExpr, rightExpr as IntExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "($left ** $right)"
        }
    }

    data class IsZero(val input: Expression) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return if (inputExpr.isBool) {
                context.mkEq(inputExpr, context.mkBool(false))
            } else {
                context.mkEq(inputExpr, context.mkInt(0))
            }
        }

        override fun subexpressions(): List<Expression> {
            return listOf(input)
        }

        override fun toString(): String {
            return "0 == $input"
        }
    }

    data class Hash(val input: Expression, val method: String) : Expression {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            val hashFunction = referenceMapping.hashFunctions.getOrPut("HASH_$method") {
                // TODO encode that x != y implies HASH(x) != HASH(y)
                context.mkFuncDecl("HASH_$method", context.intSort, context.intSort)
            }
            return context.mkApp(hashFunction, inputExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(input)
        }

        override fun toString(): String {
            return "HASH_$method($input)"
        }
    }

    companion object {
        private val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == True || (it is Value && it.constant >= 0) }
        private val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == False || it == Value(0) }
        fun constructAnd(inputs: List<Expression>): Expression {
            val distinctInputs = inputs.distinct().filterNot(IS_EQUIVALENT_TRUE)
            val falseInputs = distinctInputs.count(IS_EQUIVALENT_FALSE)
            when {
                falseInputs > 0 -> return False
                distinctInputs.isEmpty() -> return True
                distinctInputs.size == 1 -> return distinctInputs.single()
            }
            return And(distinctInputs)
        }

        fun constructOr(inputs: List<Expression>): Expression {
            val distinctInputs = inputs.distinct().filterNot(IS_EQUIVALENT_FALSE)
            val trueInputs = distinctInputs.count(IS_EQUIVALENT_TRUE)
            when {
                trueInputs > 0 -> return True
                distinctInputs.isEmpty() -> return True
                distinctInputs.size == 1 -> return distinctInputs.single()
            }
            return Or(distinctInputs)
        }
    }
}
