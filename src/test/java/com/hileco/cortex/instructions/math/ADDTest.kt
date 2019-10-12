package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import org.graalvm.compiler.core.common.type.ArithmeticOpTable
import org.junit.Assert
import org.junit.Test
import java.util.*

class ADDTest : InstructionTest() {

    @Test
    fun symbolicValueToValue() {
        val result = runSymbolic(ADD(), Value(1), Value(1))
        Assert.assertEquals(Value(2), result)
    }

    @Test
    fun symbolicValueToAddVariableToValue() {
        val result = runSymbolic(ADD(), Value(1), ArithmeticOpTable.BinaryOp.Add(Stack(0), Value(1)))
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

    @Test
    fun symbolicValueToAddValuetoVariable() {
        val result = runSymbolic(ADD(), Value(1), Add(Value(1), Stack(0)))
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

}
