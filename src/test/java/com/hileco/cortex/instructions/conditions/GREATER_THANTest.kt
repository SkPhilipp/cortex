package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression.True
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class GREATER_THANTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(100)),
                GREATER_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/greater-than")
                .headingParagraph("GREATER_THAN").paragraph("The GREATER_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was greater than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    fun symbolicGreaterThanValueToValue() {
        val result = runSymbolic(GREATER_THAN(), Value(2), Value(1))
        Assert.assertEquals(True, result)
    }
}
