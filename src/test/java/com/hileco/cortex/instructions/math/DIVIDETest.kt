package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import org.junit.Assert
import org.junit.Test

class DIVIDETest : InstructionTest() {

    @Test
    fun symbolicValueToValue() {
        val result = runSymbolic(DIVIDE(), Value(6), Value(3))
        Assert.assertEquals(Value(2), result)
    }
}
