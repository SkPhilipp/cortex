package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test

class MODULOTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(3)),
                PUSH(byteArrayOf(10)),
                MODULO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/modulo")
                .headingParagraph("MODULO").paragraph("The MODULO operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        "resulting remainder on the stack. (This result may overflow if it would have been larger than ${VirtualMachine.NUMERICAL_LIMIT})")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(1))
    }
}
