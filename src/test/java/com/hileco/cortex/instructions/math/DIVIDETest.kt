package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test

class DIVIDETest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(20)),
                PUSH(byteArrayOf(100)),
                DIVIDE())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/divide")
                .headingParagraph("DIVIDE").paragraph("The DIVIDE operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        "resulting quotient on the stack. (This result may overflow if it would have been larger than ${VirtualMachine.NUMERICAL_LIMIT})")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(5))
    }
}