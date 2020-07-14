package com.hileco.cortex.symbolic

import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_NOT
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.BITWISE_XOR
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.GREATER_THAN
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.conditions.LESS_THAN
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.math.*
import com.hileco.cortex.vm.instructions.stack.DUPLICATE
import com.hileco.cortex.vm.instructions.stack.PUSH
import com.hileco.cortex.vm.instructions.stack.SWAP
import java.math.BigInteger

class ExpressionGenerator {
    private val expressionOptimizer = ExpressionOptimizer()
    private val stack = LayeredVmStack<Expression>()
    private var missing: Int = 0

    val currentExpression: Expression
        get() {
            return if (stack.isEmpty()) Stack(0) else stack.peek()
        }

    fun viewExpression(offset: Int): Expression {
        return stack.peek(offset)
    }

    fun viewAllExpressions(): VmStack<Expression> {
        return stack
    }

    private fun pop(): Expression {
        return if (stack.isEmpty()) Stack(missing++) else stack.pop()
    }

    fun addInstruction(instruction: Instruction) {
        when (instruction) {
            is ADD -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Add(left, right)))
            }
            is SUBTRACT -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Subtract(left, right)))
            }
            is MULTIPLY -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Multiply(left, right)))
            }
            is DIVIDE -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Divide(left, right)))
            }
            is MODULO -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Modulo(left, right)))
            }
            is EXPONENT -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Exponent(left, right)))
            }
            is EQUALS -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(Equals(left, right)))
            }
            is GREATER_THAN -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(GreaterThan(left, right)))
            }
            is LESS_THAN -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(LessThan(left, right)))
            }
            is BITWISE_AND -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(BitwiseAnd(left, right)))
            }
            is BITWISE_OR -> {
                val left = pop()
                val right = pop()
                stack.push(expressionOptimizer.optimize(BitwiseOr(left, right)))
            }
            is IS_ZERO -> {
                val input = pop()
                stack.push(expressionOptimizer.optimize(IsZero(input)))
            }
            is HASH -> {
                val input = pop()
                stack.push(expressionOptimizer.optimize(Hash(input, instruction.method)))
            }
            is BITWISE_XOR -> throw UnsupportedOperationException()
            is BITWISE_NOT -> throw UnsupportedOperationException()
            is SAVE -> throw UnsupportedOperationException()
            is LOAD -> stack.push(Reference(instruction.programStoreZone, pop()))
            is PUSH -> {
                val value = BigInteger(instruction.bytes)
                stack.push(Value(value.toLong()))
            }
            is DUPLICATE -> stack.duplicate(instruction.topOffset)
            is SWAP -> try {
                stack.swap(instruction.topOffsetLeft, instruction.topOffsetRight)
            } catch (e: IndexOutOfBoundsException) {
                throw IllegalStateException(e)
            }
            else -> for (it in instruction.stackParameters) {
                pop()
            }
        }
    }
}
