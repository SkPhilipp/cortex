package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression.True
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class IS_ZEROTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(0)),
                IS_ZERO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/is-zero")
                .headingParagraph("IS_ZERO").paragraph("The IS_ZERO operation removes the top element of the stack then adds a 1 or 0 to the stack" + " depending on whether the element was equal to 0.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    fun symbolicIsZeroValue() {
        val result = runSymbolic(IS_ZERO(), Value(0))
        Assert.assertEquals(True, result)
    }
}
