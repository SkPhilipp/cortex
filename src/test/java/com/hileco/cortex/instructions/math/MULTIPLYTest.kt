package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class MULTIPLYTest : InstructionTest() {

    @Test
    fun symbolicMultiplyValueToValue() {
        val result = runSymbolic(MULTIPLY(), Value(10), Value(10))
        Assert.assertEquals(Value(100), result)
    }
}
