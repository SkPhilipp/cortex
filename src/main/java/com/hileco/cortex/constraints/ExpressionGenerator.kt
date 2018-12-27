package com.hileco.cortex.constraints

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.bits.BITWISE_AND
import com.hileco.cortex.instructions.bits.BITWISE_NOT
import com.hileco.cortex.instructions.bits.BITWISE_OR
import com.hileco.cortex.instructions.bits.BITWISE_XOR
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.GREATER_THAN
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.conditions.LESS_THAN
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.math.*
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.layer.LayeredStack
import com.microsoft.z3.*
import java.math.BigInteger

class ExpressionGenerator {
    private val stack = LayeredStack<Expression>()
    private var missing: Int = 0

    val currentExpression: Expression
        get() {
            return stack.peek() ?: Stack(0)
        }

    fun viewExpression(offset: Int): Expression {
        return stack[stack.size() - offset]!!
    }

    fun viewAllExpressions(): LayeredStack<Expression> {
        return stack
    }

    private fun pop(): Expression {
        return if (stack.size() > 0) stack.pop()!! else Stack(missing++)
    }

    private fun leftRight(representation: String, converter: (Context, Expr, Expr) -> Expr) {
        val left = pop()
        val right = pop()
        stack.push(LeftRight(representation, converter, left, right))
    }

    private fun input(representation: String, converter: (Context, Expr) -> Expr) {
        val input = pop()
        stack.push(Input(representation, converter, input))
    }

    fun addInstruction(instruction: Instruction) {
        when (instruction) {
            is ADD -> leftRight("+") { context, left, right -> context.mkAdd(left as ArithExpr, right as ArithExpr) }
            is SUBTRACT -> leftRight("-") { context, left, right -> context.mkSub(left as ArithExpr, right as ArithExpr) }
            is MULTIPLY -> leftRight("*") { context, left, right -> context.mkMul(left as ArithExpr, right as ArithExpr) }
            is DIVIDE -> leftRight("/") { context, left, right -> context.mkDiv(left as ArithExpr, right as ArithExpr) }
            is LESS_THAN -> leftRight("<") { context, left, right -> context.mkLt(left as ArithExpr, right as ArithExpr) }
            is GREATER_THAN -> leftRight(">") { context, left, right -> context.mkGt(left as ArithExpr, right as ArithExpr) }
            is EQUALS -> leftRight("==") { context, left, right -> context.mkEq(left, right) }
            is BITWISE_OR -> leftRight("||") { context, left, right -> context.mkOr(left as BoolExpr, right as BoolExpr) }
            is BITWISE_AND -> leftRight("&&") { context, left, right -> context.mkAnd(left as BoolExpr, right as BoolExpr) }
            is MODULO -> leftRight("%") { context, left, right -> context.mkMod(left as IntExpr, right as IntExpr) }
            is IS_ZERO -> input("0 == ") { context, input ->
                if (input.isBool) {
                    context.mkEq(input, context.mkBool(false))
                } else {
                    context.mkEq(input, context.mkInt(0))
                }
            }
            is HASH -> throw UnsupportedOperationException()
            is BITWISE_XOR -> throw UnsupportedOperationException()
            is BITWISE_NOT -> throw UnsupportedOperationException()
            is SAVE -> throw UnsupportedOperationException()
            is LOAD -> stack.push(Reference(instruction.programStoreZone, pop()))
            is PUSH -> {
                val value = BigInteger(instruction.bytes)
                stack.push(Value(value.toLong()))
            }
            is DUPLICATE -> stack.duplicate(instruction.position)
            is SWAP -> try {
                stack.swap(instruction.topOffsetLeft, instruction.topOffsetRight)
            } catch (e: IndexOutOfBoundsException) {
                throw ImpossibleExpressionException(e)
            }
            else -> for (it in instruction.stackParameters) {
                pop()
            }
        }
    }
}
