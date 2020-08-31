package com.hileco.cortex.symbolic.expressions

import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.microsoft.z3.BitVecExpr
import com.microsoft.z3.BoolExpr
import com.microsoft.z3.Context
import com.microsoft.z3.Expr

abstract class Expression {

    /*
        A comment on Z3's convention, as this class interfaces with Z3. Z3's interface may not be immediately
        obvious; Z3 likes to abbreviate antyhing and everything possible to abbreviate.

        Methods (for creating expressions) often follow the convention `make ($type)? (signed|unsigned)? $operation`.
        This means to create a less-than on two bit vectors "make bit-vector unsigned less-than" would be needed.

        This is abbreviated as `mk BV U LT`; `mkBVULT`.
        Keep this convention in mind when reading or modifying this class.
     */

    abstract fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr

    abstract fun subexpressions(): List<Expression>

    data class Reference(val type: ProgramStoreZone,
                         val address: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val reference = referenceMapping.referencesForward.computeIfAbsent(this@Reference) { unmappedReference ->
                val key = referenceMapping.referencesForward.size.toString()
                referenceMapping.referencesBackward[key] = unmappedReference
                key
            }
            val referenceSymbol = context.mkSymbol(reference)
            return context.mkBVConst(referenceSymbol, BIT_VECTOR_SIZE)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(address)
        }

        override fun toString(): String {
            return "Reference($type, $address)"
        }
    }

    data class Value(val constant: BackedInteger) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            // TODO: Decimal string representation of the bit vector can be passed here
            return context.mkBV(constant.toLong(), BIT_VECTOR_SIZE)
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            val constantTrimmed = constant.toString().replaceFirst("^0+(?!$)".toRegex(), "")
            return "Value($constantTrimmed)"
        }
    }

    data class Stack(val address: Int = 0) : Expression() {
        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            throw IllegalArgumentException("Missing stack: $address.")
        }

        override fun toString(): String {
            return "Stack($address)"
        }
    }

    data class Not(val input: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return context.mkNot(inputExpr as BoolExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(input)
        }

        override fun toString(): String {
            return "Not($input)"
        }
    }

    data class And(val inputs: List<Expression>) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkAnd(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun subexpressions(): List<Expression> {
            return inputs
        }

        override fun toString(): String {
            return "And(${subexpressions().joinToString()})"
        }
    }

    data class Or(val inputs: List<Expression>) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkOr(*inputs.map { it.asZ3Expr(context, referenceMapping) as BoolExpr }.toTypedArray())
        }

        override fun subexpressions(): List<Expression> {
            return inputs
        }

        override fun toString(): String {
            return "Or(${subexpressions().joinToString()})"
        }
    }

    object True : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkTrue()
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            return "True"
        }
    }

    object False : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            return context.mkFalse()
        }

        override fun subexpressions(): List<Expression> {
            return listOf()
        }

        override fun toString(): String {
            return "False"
        }
    }

    data class Add(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVAdd(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Add($left, $right)"
        }
    }

    data class Subtract(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVSub(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Subtract($left, $right)"
        }
    }

    data class Multiply(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVMul(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Multiply($left, $right)"
        }
    }

    data class Divide(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVUDiv(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Divide($left, $right)"
        }
    }

    data class LessThan(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVULT(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "LessThan($left, $right)"
        }
    }

    data class GreaterThan(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVUGT(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "GreaterThan($left, $right)"
        }
    }

    data class Equals(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkEq(leftExpr, rightExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Equals($left, $right)"
        }
    }

    data class BitwiseOr(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVOR(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "BitwiseOr($left, $right)"
        }
    }

    data class BitwiseNot(val element: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val elementExpr = element.asZ3Expr(context, referenceMapping)
            return context.mkBVNot(elementExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(element)
        }

        override fun toString(): String {
            return "BitwiseNot($element)"
        }
    }

    data class BitwiseAnd(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVAND(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "BitwiseAnd($left, $right)"
        }
    }

    data class ShiftRight(val times: Expression, val value: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = times.asZ3Expr(context, referenceMapping)
            val rightExpr = value.asZ3Expr(context, referenceMapping)
            return context.mkBVLSHR(rightExpr as BitVecExpr, leftExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(times, value)
        }

        override fun toString(): String {
            return "ShiftRight($times, $value)"
        }
    }

    data class Modulo(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val leftExpr = left.asZ3Expr(context, referenceMapping)
            val rightExpr = right.asZ3Expr(context, referenceMapping)
            return context.mkBVURem(leftExpr as BitVecExpr, rightExpr as BitVecExpr)
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Modulo($left, $right)"
        }
    }

    data class Exponent(val left: Expression, val right: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            @Suppress("UNUSED_VARIABLE") val leftExpr = left.asZ3Expr(context, referenceMapping)
            @Suppress("UNUSED_VARIABLE") val rightExpr = right.asZ3Expr(context, referenceMapping)
            // TODO: Implement
            throw IllegalStateException("Not yet available for bit-vectors")
        }

        override fun subexpressions(): List<Expression> {
            return listOf(left, right)
        }

        override fun toString(): String {
            return "Exponent($left, $right)"
        }
    }

    data class IsZero(val input: Expression) : Expression() {
        override fun asZ3Expr(context: Context, referenceMapping: ReferenceMapping): Expr {
            val inputExpr = input.asZ3Expr(context, referenceMapping)
            return if (inputExpr.isBool) {
                context.mkEq(inputExpr, context.mkBool(false))
            } else {
                context.mkEq(inputExpr, context.mkBV(0, BIT_VECTOR_SIZE))
            }
        }

        override fun subexpressions(): List<Expression> {
            return listOf(input)
        }

        override fun toString(): String {
            return "IsZero($input)"
        }
    }

    data class Hash(val input: Expression, val method: String) : Expression() {
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
            return "Hash(\"$method\", $input)"
        }
    }

    companion object {
        private const val BIT_VECTOR_SIZE = 256
        private val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == True || (it is Value && it.constant >= ZERO_32) }
        private val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == False || it == Value(ZERO_32) }
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
