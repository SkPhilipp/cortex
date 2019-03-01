package com.hileco.cortex.constraints

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.bits.BITWISE_NOT
import com.hileco.cortex.instructions.bits.BITWISE_XOR
import com.hileco.cortex.instructions.bits.BitInstruction
import com.hileco.cortex.instructions.conditions.ConditionInstruction
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.math.HASH
import com.hileco.cortex.instructions.math.MathInstruction
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class ExpressionGenerator {
    private val stack = LayeredStack<Expression>()
    private var missing: Int = 0

    val currentExpression: Expression
        get() {
            return if (stack.isEmpty()) Stack(0) else stack.peek()
        }

    fun viewExpression(offset: Int): Expression {
        return stack.peek(offset)
    }

    fun viewAllExpressions(): LayeredStack<Expression> {
        return stack
    }

    private fun pop(): Expression {
        return if (stack.isEmpty()) Stack(missing++) else stack.pop()
    }

    fun addInstruction(instruction: Instruction) {
        when (instruction) {
            is MathInstruction -> {
                val left = pop()
                val right = pop()
                stack.push(instruction.calculate(left, right))
            }
            is ConditionInstruction -> {
                val left = pop()
                val right = pop()
                stack.push(instruction.innerExecute(left, right))
            }
            is BitInstruction -> {
                val left = pop()
                val right = pop()
                stack.push(instruction.innerExecute(left, right))
            }
            is IS_ZERO -> {
                val input = pop()
                stack.push(instruction.innerExecute(input))
            }
            is HASH -> {
                val input = pop()
                stack.push(instruction.innerExecute(input))
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
                throw ImpossibleExpressionException(e)
            }
            else -> for (it in instruction.stackParameters) {
                pop()
            }
        }
    }
}
