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
    fun symbolicGreaterThanValueToValue() {
        val result = runSymbolic(GREATER_THAN(), Value(2), Value(1))
        Assert.assertEquals(True, result)
    }
}
